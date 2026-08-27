from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, timezone
from enum import Enum
from threading import RLock
from typing import Iterable


class TaskState(str, Enum):
    CREATED = "CREATED"
    QUEUED = "QUEUED"
    RUNNING = "RUNNING"
    SUCCESS = "SUCCESS"
    FAILURE = "FAILURE"
    RETRY = "RETRY"
    TIMEOUT = "TIMEOUT"
    RECOVERY = "RECOVERY"
    CANCELLED = "CANCELLED"
    FINAL_FAILURE = "FINAL_FAILURE"


TRANSITIONS = {
    TaskState.CREATED: {TaskState.QUEUED, TaskState.CANCELLED},
    TaskState.QUEUED: {TaskState.RUNNING, TaskState.CANCELLED},
    TaskState.RUNNING: {TaskState.SUCCESS, TaskState.FAILURE, TaskState.TIMEOUT, TaskState.CANCELLED},
    TaskState.FAILURE: {TaskState.RETRY, TaskState.FINAL_FAILURE, TaskState.RECOVERY},
    TaskState.RETRY: {TaskState.QUEUED, TaskState.CANCELLED},
    TaskState.TIMEOUT: {TaskState.RECOVERY, TaskState.RETRY, TaskState.FINAL_FAILURE, TaskState.CANCELLED},
    TaskState.RECOVERY: {TaskState.QUEUED, TaskState.FINAL_FAILURE, TaskState.CANCELLED},
    TaskState.SUCCESS: set(),
    TaskState.CANCELLED: set(),
    TaskState.FINAL_FAILURE: set(),
}


@dataclass
class Task:
    task_id: str
    agent: str
    dependencies: tuple[str, ...] = ()
    max_retries: int = 0
    state: TaskState = TaskState.CREATED
    attempts: int = 0
    error: str = ""
    history: list[TaskState] = field(default_factory=lambda: [TaskState.CREATED])
    updated_at: str = field(default_factory=lambda: datetime.now(timezone.utc).isoformat())


class TaskOrchestrator:
    def __init__(self):
        self._tasks: dict[str, Task] = {}
        self._lock = RLock()
        self._running: set[str] = set()

    def create(self, task_id: str, agent: str, dependencies: Iterable[str] = (), max_retries: int = 0) -> Task:
        with self._lock:
            if not task_id or task_id in self._tasks:
                raise ValueError("duplicate_or_empty_task_id")
            if max_retries < 0:
                raise ValueError("negative_max_retries")
            deps = tuple(dependencies)
            if task_id in deps or any(dep not in self._tasks for dep in deps):
                raise ValueError("unknown_or_circular_dependency")
            task = Task(task_id, agent, deps, max_retries)
            self._tasks[task_id] = task
            return task

    def get(self, task_id: str) -> Task:
        with self._lock:
            if task_id not in self._tasks:
                raise KeyError(task_id)
            return self._tasks[task_id]

    def transition(self, task_id: str, target: TaskState, error: str = "") -> Task:
        with self._lock:
            task = self.get(task_id)
            if target not in TRANSITIONS[task.state]:
                raise ValueError(f"invalid_transition:{task.state.value}->{target.value}")
            if target == TaskState.QUEUED:
                if any(self.get(dep).state != TaskState.SUCCESS for dep in task.dependencies):
                    raise ValueError("dependencies_not_satisfied")
            if target == TaskState.RUNNING:
                if task.task_id in self._running:
                    raise ValueError("duplicate_execution_prevented")
                self._running.add(task.task_id)
                task.attempts += 1
            if task.state == TaskState.RUNNING and target != TaskState.RUNNING:
                self._running.discard(task.task_id)
            task.state = target
            task.error = error
            task.history.append(target)
            task.updated_at = datetime.now(timezone.utc).isoformat()
            return task

    def queue(self, task_id: str) -> Task:
        return self.transition(task_id, TaskState.QUEUED)

    def start(self, task_id: str) -> Task:
        return self.transition(task_id, TaskState.RUNNING)

    def succeed(self, task_id: str) -> Task:
        return self.transition(task_id, TaskState.SUCCESS)

    def fail(self, task_id: str, error: str) -> Task:
        task = self.get(task_id)
        self.transition(task_id, TaskState.FAILURE, error)
        target = TaskState.RETRY if task.attempts <= task.max_retries else TaskState.FINAL_FAILURE
        return self.transition(task_id, target, error)

    def timeout(self, task_id: str, error: str = "timeout") -> Task:
        return self.transition(task_id, TaskState.TIMEOUT, error)

    def recover(self, task_id: str) -> Task:
        return self.transition(task_id, TaskState.RECOVERY)

    def cancel(self, task_id: str) -> Task:
        return self.transition(task_id, TaskState.CANCELLED, "cancelled")

    def snapshot(self) -> dict[str, dict[str, object]]:
        with self._lock:
            return {
                task_id: {
                    "agent": task.agent,
                    "state": task.state.value,
                    "attempts": task.attempts,
                    "dependencies": task.dependencies,
                    "history": [state.value for state in task.history],
                    "error": task.error,
                    "updated_at": task.updated_at,
                }
                for task_id, task in self._tasks.items()
            }

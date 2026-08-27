from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "qa" / "agents"))
from task_orchestrator import TaskOrchestrator, TaskState


def main():
    engine = TaskOrchestrator()
    first = engine.create("first", "QA", max_retries=1)
    assert first.state == TaskState.CREATED
    engine.queue("first")
    engine.start("first")
    try:
        engine.start("first")
        raise AssertionError("duplicate start was accepted")
    except ValueError as error:
        assert str(error) == "invalid_transition:RUNNING->RUNNING"
    engine.fail("first", "controlled failure")
    assert engine.get("first").state == TaskState.RETRY
    engine.queue("first")
    engine.start("first")
    engine.succeed("first")
    assert engine.get("first").history == [TaskState.CREATED, TaskState.QUEUED, TaskState.RUNNING, TaskState.FAILURE, TaskState.RETRY, TaskState.QUEUED, TaskState.RUNNING, TaskState.SUCCESS]

    pending = engine.create("pending", "QA")
    second = engine.create("second", "Security", dependencies=("pending",))
    try:
        engine.queue("second")
        raise AssertionError("dependency gate was bypassed")
    except ValueError as error:
        assert str(error) == "dependencies_not_satisfied"
    engine.queue("pending")
    engine.start("pending")
    engine.succeed("pending")
    engine.queue("second")
    engine.start("second")
    engine.timeout("second")
    engine.recover("second")
    engine.queue("second")
    engine.start("second")
    engine.succeed("second")

    third = engine.create("third", "Chaos")
    engine.queue("third")
    engine.cancel("third")
    try:
        engine.start("third")
        raise AssertionError("cancelled task restarted")
    except ValueError as error:
        assert str(error) == "invalid_transition:CANCELLED->RUNNING"

    fourth = engine.create("fourth", "QA", max_retries=0)
    engine.queue("fourth")
    engine.start("fourth")
    engine.fail("fourth", "final controlled failure")
    assert engine.get("fourth").state == TaskState.FINAL_FAILURE
    print("task_orchestrator_tests=4")
    print("retry_success_path=true")
    print("dependency_gate=true")
    print("timeout_recovery=true")
    print("cancel_and_final_failure=true")


if __name__ == "__main__":
    main()

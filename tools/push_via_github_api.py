import base64
import json
import re
import subprocess
from pathlib import Path

REPO = "itel92673-bit/Dark-Med-"
HEAD = subprocess.check_output(["git", "rev-parse", "HEAD"], text=True).strip()
MESSAGE = subprocess.check_output(["git", "log", "-1", "--format=%B"], text=True).strip()

def gh_api(args, payload=None):
    command = ["gh", "api", *args]
    if payload is None:
        output = subprocess.check_output(command, text=True)
    else:
        result = subprocess.run(command + ["--input", "-"], input=json.dumps(payload), text=True, capture_output=True)
        if result.returncode != 0:
            raise RuntimeError(result.stderr.strip())
        output = result.stdout
    output = re.sub(r"\x1b\[[0-9;]*[A-Za-z]", "", output)
    return json.loads(output)

remote_ref = gh_api([f"repos/{REPO}/git/ref/heads/main"])
parent = remote_ref["object"]["sha"]
parent_commit = gh_api([f"repos/{REPO}/git/commits/{parent}"])
entries = []
files = subprocess.check_output(["git", "ls-tree", "-r", "--name-only", HEAD], text=True).splitlines()
for name in files:
    data = Path(name).read_bytes()
    blob = gh_api([f"repos/{REPO}/git/blobs", "--method", "POST"], {
        "content": base64.b64encode(data).decode("ascii"),
        "encoding": "base64",
    })
    entries.append({"path": name, "mode": "100644", "type": "blob", "sha": blob["sha"]})

tree = gh_api([f"repos/{REPO}/git/trees", "--method", "POST"], {
    "base_tree": parent_commit["tree"]["sha"],
    "tree": entries,
})
commit = gh_api([f"repos/{REPO}/git/commits", "--method", "POST"], {
    "message": MESSAGE,
    "tree": tree["sha"],
    "parents": [parent],
})
gh_api([f"repos/{REPO}/git/refs/heads/main", "--method", "PATCH"], {"sha": commit["sha"], "force": False})
print(json.dumps({"local_head": HEAD, "remote_commit": commit["sha"], "remote_tree": tree["sha"], "parent": parent}, indent=2))

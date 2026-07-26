# Windows Git setup

This archive uses a single project root to avoid Windows path-length failures.

Recommended extraction location:

```text
D:\code\fintech-settlement-engine
```

Then run:

```powershell
cd D:\code\fintech-settlement-engine
git config --global core.longpaths true
git add --renormalize .
git status
```

The `LF will be replaced by CRLF` message is a line-ending warning, not the cause of `git add` failing. The included `.gitattributes` file defines the repository line-ending policy.

# AGENTS Context Map

## Current project

- Repo (Windows): `C:\Users\3stra\AndroidStudioProjects\TicTacToeSignalR`
- Repo (WSL mount): `/mnt/c/Users/3stra/AndroidStudioProjects/TicTacToeSignalR`
- Purpose:
  - Sandbox-first Android project for rapid experiments.
  - Prototype here first, then transfer the validated flow to `TicTacMenu`.

## Teacher project relation source

- Reference map source (Jekyll teacher repo):
  - `\\wsl.localhost\Ubuntu\home\stra\repos\BeautifulMivney\AGENTS.md`
- Jekyll repo paths:
  - Windows UNC: `\\wsl.localhost\Ubuntu\home\stra\repos\BeautifulMivney`
  - WSL: `/home/stra/repos/BeautifulMivney`
- Android lesson pages in teacher repo are mainly under:
  - `/home/stra/repos/BeautifulMivney/android/projectSteps`
- Bagrut assets used by teacher pages:
  - `/home/stra/repos/BeautifulMivney/bagruyot`

## Main paired Android project

- Validation/teaching project:
  - Windows: `C:\Users\3stra\AndroidStudioProjects\TicTacMenu`
  - WSL mount: `/mnt/c/Users/3stra/AndroidStudioProjects/TicTacMenu`
- Keep this sandbox aligned with `TicTacMenu` for:
  - package layout (`activities`, `models`, `services`, `shell`)
  - launcher shell flow (`MenuActivity` + drawer + fragments)
  - network settings used in lessons

## Workflow contract

1. Prototype/de-risk in `TicTacToeSignalR`.
2. Document/refine lesson flow in `BeautifulMivney` (usually under `android/projectSteps`).
3. Apply same flow in `TicTacMenu` with controlled commit phases.

## Sibling and shared repos

- `BeautifulYesodot`
  - Windows UNC: `\\wsl.localhost\Ubuntu\home\stra\repos\BeautifulYesodot`
  - WSL: `/home/stra/repos/BeautifulYesodot`
- `mathBeautifulFork`
  - Windows UNC: `\\wsl.localhost\Ubuntu\home\stra\sites\mathBeautifulFork`
  - WSL: `/home/stra/sites/mathBeautifulFork`
- Shared utility `bag_splitter`
  - WSL: `/home/stra/repos/bag_splitter`

## Cross-filesystem access fallback

- Prefer direct UNC reads from PowerShell when available.
- If UNC or mounted path access fails, use WSL commands, for example:
  - `wsl bash -lc "ls /mnt/c/Users/3stra/AndroidStudioProjects/TicTacMenu"`
  - `wsl bash -lc "sed -n '1,200p' /home/stra/repos/BeautifulMivney/AGENTS.md"`
- Note for this repo update: teacher `AGENTS.md` was reachable directly over UNC, so WSL fallback was not required for this run.

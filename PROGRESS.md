# Local Deployment Progress

- Goal: Run the PmHub monolithic app on Windows and keep it usable from IntelliJ IDEA.
- Backend: Built `pmhub-boot/pmhub-admin` with JDK 8 and started PID 79444 on `http://127.0.0.1:5010`.
- Database: MySQL is running as PID 67708 on `127.0.0.1:3306`; schema `laigeoffer-pmhub` has 93 tables. The import SQL's final `CREATE TABLE` statement was missing a semicolon and is fixed.
- Cache: Redis is running as PID 78824 on `127.0.0.1:6379`, with data isolated under `C:\Users\adminZTY\AppData\Local\Temp\pmhub-redis-b6f825717eb94b97b030b0e5bb32fc29`.
- Runtime files: API logs/uploads are under `C:\Users\adminZTY\AppData\Local\Temp\pmhub-run-b7268bb64d2546049dda382feee78f5c`.
- Frontend: Dependencies installed and Vue CLI compiled successfully; dev server is running at `http://localhost:3000` (npm PID 44552).
- IDEA: Opened `pmhub-boot` in IntelliJ IDEA. Import `pmhub-boot/pom.xml`; set Project SDK and Maven runner JDK to `C:\Program Files\Java\jdk1.8.0_202`; main class is `com.laigeoffer.pmhub.PmhubApplication`.
- Changed files: `pmhub-boot/sql/pmhub_20240305.sql` (trailing semicolon); `pmhub-boot/pmhub-ui/package.json` (Windows-compatible `dev` script and missing `tributejs` dependency).
- Verified: Maven package succeeds; Redis returns `PONG`; Swagger UI, frontend HTML/JS assets, and frontend-proxied `/dev-api/captchaImage` return HTTP 200.
- Runtime: MySQL PID 67708, Redis PID 78824, backend PID 79444, frontend npm PID 44552. Stop these processes from their respective run configurations or via Task Manager when finished.
- Remaining: None for the local startup. JDK 8 is required for running/building in IDEA; Node dependencies are installed under the single-app frontend.

## Startup Guide

- Goal: Provide a detailed Chinese HTML guide for starting the monolith and microservice editions from the command line and IntelliJ IDEA.
- Changed: Added `docs/本地启动与IDEA操作说明.html` with module entry points, ports, SQL import commands, IDEA JDK/Maven settings, frontend commands, and troubleshooting.
- Credentials: Verified the seeded local `admin` password `123456` against the BCrypt hashes in both monolith and microservice SQL. The guide distinguishes local seed credentials from online demo credentials, which the README says to obtain via public account 「苍何」 and reply `pmhub`.
- Verification: HTML structure, command references, and seeded admin password checked against repository configuration; BCrypt matches confirmed with the project security library.

## Redis Startup Guide

- Goal: Add safe, detailed local Redis startup instructions to the existing Chinese HTML guide.
- Changed: Expanded `docs/本地启动与IDEA操作说明.html` with the installed Windows Redis 5.0.14.1 foreground command, `PONG` check, IDEA Terminal steps, local connection/password guidance, Docker alternative, normal shutdown, and troubleshooting.
- Constraints: Keep Redis bound to `127.0.0.1`; do not modify application/Nacos configuration. The Docker image example is illustrative and was not run or compatibility-tested.
- Verification: Checked the local Redis install path, server/client executables, single-app Redis config, and microservice example config. Redis server help confirms direct CLI startup is supported; HTML section/detail tags are balanced and all 14 local anchors resolve. This documentation pass did not start or stop Redis.
- Next: Run the documented foreground command when Redis is needed; confirm `PONG` before starting the Java backend.

## Manual electricity registration page

- Goal: Add a System Monitoring page for manually maintaining daily electricity usage by area, with add/edit/view/list functions.
- Changed: Added `pmhub-boot/pmhub-system` domain/mapper/service, `pmhub-boot/pmhub-admin` controller, frontend API/page, and deployable SQL migration `pmhub-boot/sql/monitor_manual_electricity.sql`.
- Data model: One row per area/date; `energy_kwh` is non-negative with up to 3 decimal places; date+area is unique. Delete was intentionally not added because it was not requested.
- Menu/permissions: Migration creates the page under System Monitoring plus query/add/edit permissions and grants them to active admin roles.
- Verification: Backend `mvn -pl pmhub-admin -am -DskipTests package` passed with JDK 8; frontend `NODE_OPTIONS=--openssl-legacy-provider npm run build:prod` passed with existing unrelated warnings; focused ESLint passed with no warnings; mapper XML parsed successfully; `git diff --check` passed. The migration was also applied to the local `laigeoffer-pmhub` database using MySQL `--default-character-set=utf8mb4`; the table and four menu/permission rows were verified.
- Known constraint: The repository's default Node build fails without `NODE_OPTIONS=--openssl-legacy-provider`; the project already uses this flag in its dev script.
# LineupLab

LineupLab is a native Android app for coaching recreational sports. It lets
coaches visualize team formations, build custom player lineups, save preset
configurations, and track player position history for data-driven lineup
decisions. The MVP targets soccer (standard 1-11 position numbering); the
architecture is sport-agnostic for future expansion.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Room for local persistence (offline-first, no cloud)
- ViewModel + Repository pattern
- Gradle version catalog (`gradle/libs.versions.toml`), KSP, kotlinx.serialization

## Project layout

```
app/src/main/java/com/lineuplab/app/
├── domain/
│   ├── model/          # PositionMapping, FormationSlot, RoleCategory, FormationType
│   └── sport/          # SportConfig contract, SportRegistry, baked-in SoccerConfig
├── data/
│   ├── local/          # Room database, DAOs, entities, seeder, JSON codec
│   └── repository/     # Team / Formation / Lineup / PlayingHistory repositories
└── ui/                 # Compose UI (theme, screens, view models)
```

## Data model

- **Sport** — seeded row per supported sport; position mappings and field
  layout are baked-in constants keyed by sport name (`SportRegistry`).
- **Team** / **Player** — roster with default position numbers (1-11).
- **Formation** — standard formations (4-4-2, 4-3-3, 3-5-2, 4-2-3-1, 5-3-2)
  are seeded globally (`team_id IS NULL`); custom formations belong to a team.
  Slots (position number + normalized x/y field coordinates) are stored as JSON.
- **Lineup** + **LineupAssignment** — saved presets mapping positions to players.
- **PlayingHistory** — one record per player-position pair each time a lineup
  is set as played; source of truth for analytics.
- **PlayerPositionStats** — cached appearance counts per player/position,
  updated transactionally with history writes.

## Building

```
./gradlew assembleDebug
```

Requires JDK 17+ and the Android SDK (compileSdk 35). Every push to `dev`,
`main`, or a `claude/**` branch runs the Android CI workflow, which executes
unit tests and uploads a debug APK artifact (`lineuplab-debug-apk`) for
rapid sideload testing.

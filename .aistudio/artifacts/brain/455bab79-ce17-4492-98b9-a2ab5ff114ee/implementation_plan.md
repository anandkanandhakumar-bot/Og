# OG WARRIORS: Esports & Gaming Community App

OG WARRIORS is a complete, native Android application built with Kotlin, Jetpack Compose, and Material 3, providing an esports platform featuring gaming news, pro tips, tournaments with leaderboard and registration, video editing tutorials, user profiles, and a secure Private Super Admin panel.

## User Review & Critical Decisions

> [!IMPORTANT]
> - **Theme & Aesthetics**: Premium black (`#0A0A0C`), vibrant esports red (`#FF1744`), and electric blue (`#00E5FF`) accents with dark M3 styling.
> - **Super Admin Access**: The designated owner account (`anandkanandhakumar@gmail.com`) is granted Super Admin privileges upon Google Sign-In, securing the Admin Panel against unauthorized users via secure security rules and role validation.
> - **Backend Integration**: Firebase Authentication (Google Sign-In via Credential Manager), Cloud Firestore for real-time posts, tips, tournaments, registrations, and user profiles.

- **Confirmed Decision 1**: Use Google Sign-In via Jetpack Credential Manager for secure authentication.
- **Confirmed Decision 2**: Use Cloud Firestore with secure security rules for zero-trust data synchronization across Home, Tips, Tournaments, Editing, and Admin panel.

---

## 1. Overview & Core Concept

- **What It Does**: Delivers a fully-functional esports hub for gamers and content creators. Users can browse breaking gaming news, learn pro headshot and movement techniques, register for esports tournaments with leaderboards, master CapCut and montage video editing skills, and manage their profile. The app includes a dedicated Super Admin panel for publishing content and managing tournaments.
- **Target Audience**: Competitive mobile and PC gamers, esports enthusiasts, and gaming content creators.
- **Key Value**: Real-time tournament registration, expert training guides, community engagement (likes/comments), and robust admin control.

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Authentication**: Splash/Auth screen with "Sign in with Google" button using Credential Manager.
  2. **Home Tab**: Breaking gaming news, featured posts, announcements, and tournament quick-highlights.
  3. **Gaming Tips Tab**: Categorized guides (Headshot training, HUD sensitivity, movement) with rich step-by-step tutorials and video lessons.
  4. **Tournaments Tab**: List of upcoming and ongoing tournaments, view rules/prizes, team registration modal, and live leaderboard.
  5. **Editing Skills Tab**: Tutorials on CapCut, beat sync, velocity, color grading, and reels editing.
  6. **Profile Tab**: User avatar, saved tutorials, registered tournaments, and access button to the **Super Admin Panel** (visible only to the owner).
  7. **Super Admin Panel**: Create/edit/delete news posts, publish gaming tips & editing tutorials, manage tournaments, update results, and review user reports.
- **Visual Identity & Theme**:
  - *Aesthetic*: Cyberpunk / high-octane esports luxury dark theme.
  - *Color Palette*: Deep Obsidian Black background (`#0A0A0C`), Neon Red primary (`#FF1744`), Electric Cyan/Blue secondary (`#00E5FF`), Surface Dark (`#16161E`), and clean white text (`#F5F5F7`).
  - *Typography*: Bold display headings with high contrast, readable body text.
  - *Component Styling*: Elevated M3 cards with subtle glowing borders, gradient pill indicators for bottom navigation, ripple touch feedbacks.

## 3. Key Product Decisions & Trade-Offs

- **Decision 1**: Firebase Firestore & Auth integration with strict role-based access control.
  - *Chosen Approach*: Google Sign-In for all users, with server-side / Firestore security rules verifying admin UIDs (`anandkanandhakumar@gmail.com`).
  - *Why*: Ensures maximum security against unauthorized admin access while offering frictionless onboarding for gamers.
- **Decision 2**: Bottom Navigation with 5 primary destinations (Home, Tips, Tournaments, Editing, Profile).
  - *Chosen Approach*: Material 3 `NavigationBar` with custom icon states and safe-area padding.
  - *Why*: Fast, intuitive thumb-friendly switching between core features.

## 4. Technical Architecture & Data Strategy

```
┌──────────────────────────────────────────────────────────────┐
│                   OG WARRIORS Android App                    │
│      (Jetpack Compose M3 + Navigation Compose + ViewModels)  │
└──────────────┬──────────────────────────────┬────────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────────┐    ┌──────────────────────────┐
│      Firebase Auth           │    │    Cloud Firestore       │
│  (Google Sign-In / CredMgr)  │    │  - /posts                │
│                              │    │  - /tips                 │
│                              │    │  - /tournaments          │
│                              │    │  - /registrations        │
│                              │    │  - /editing_tutorials    │
└──────────────────────────────┘    └──────────────────────────┘
```

- **Data Models**:
  - `Post` (id, title, content, imageUrl, author, timestamp, category)
  - `Tip` (id, title, category, content, videoUrl, difficulty)
  - `Tournament` (id, name, game, prizePool, startDate, rules, status)
  - `Registration` (id, tournamentId, userId, teamName, members, status)
  - `EditingTutorial` (id, title, software, stepsUrl, category)
  - `UserProfile` (uid, displayName, email, photoUrl, role, savedTips)
- **State Management**: MVVM architecture with `ViewModel`, `StateFlow`, and `collectAsStateWithLifecycle()`.

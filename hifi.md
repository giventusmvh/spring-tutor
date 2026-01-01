# HiFi Prototype Prompt Guide

> Panduan untuk membuat prompt AI dalam pembuatan High-Fidelity Prototype untuk Loan Application System

---

## Table of Contents

1. [Overview](#1-overview)
2. [Android App (Customer)](#2-android-app-customer)
3. [Web Dashboard (Internal)](#3-web-dashboard-internal)
4. [Design System](#4-design-system)
5. [Sample Prompts](#5-sample-prompts)

---

## 1. Overview

### Aplikasi Target

| Platform          | User                                              | Deskripsi                                                                     |
| ----------------- | ------------------------------------------------- | ----------------------------------------------------------------------------- |
| **Android App**   | Customer                                          | Registrasi, lengkapi profile, pilih plafond, ajukan pinjaman, tracking status |
| **Web Dashboard** | Marketing, Branch Manager, Backoffice, SuperAdmin | Approval workflow, user management                                            |

### Tech Stack untuk Prototype

| Platform | Tools Rekomendasi       |
| -------- | ----------------------- |
| Mobile   | Figma, Adobe XD, Framer |
| Web      | Figma, Webflow, Framer  |

---

## 2. Android App (Customer)

### User Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Splash &   │ →  │  Complete   │ →  │   Select    │ →  │   Submit    │ →  │    Track    │
│  Auth       │    │   Profile   │    │   Plafond   │    │    Loan     │    │   Status    │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### Screen List

| No  | Screen                | Deskripsi               | Key Elements                                             |
| --- | --------------------- | ----------------------- | -------------------------------------------------------- |
| 1   | Splash Screen         | Logo, tagline           | Logo EHEFIN, gradient background                         |
| 2   | Login                 | Email & password form   | Input fields, "Forgot Password" link, "Register" link    |
| 3   | Register              | Nama, email, password   | Validation hints, password strength                      |
| 4   | Forgot Password       | Email input             | Success message                                          |
| 5   | Reset Password        | New password form       | Token input, confirm password                            |
| 6   | Home/Dashboard        | Overview status         | Active plafond card, recent loans, quick actions         |
| 7   | Profile               | User info, completeness | NIK, birthdate, phone, address, progress indicator       |
| 8   | Edit Profile          | Form lengkap            | Validation, date picker, save button                     |
| 9   | Product List          | Daftar plafond          | BRONZE/SILVER/GOLD/PLATINUM cards dengan limit & rate    |
| 10  | Plafond Detail        | Info produk             | Amount, tenor, interest rate, "Select" button            |
| 11  | My Plafond            | Status plafond aktif    | Progress bar (remaining/original), product info          |
| 12  | Branch List           | Pilih cabang            | Location list untuk pengajuan loan                       |
| 13  | Loan Application Form | Form pengajuan          | Amount slider, tenor picker, rate input, branch dropdown |
| 14  | Loan Confirmation     | Review sebelum submit   | Summary, terms checkbox, submit button                   |
| 15  | Loan List             | Daftar pengajuan        | Status badges, filter, search                            |
| 16  | Loan Detail           | Detail satu pengajuan   | Amount, status, timeline history                         |
| 17  | Loan History Timeline | Status approval         | Stepper dengan approver names & timestamps               |
| 18  | Settings              | Pengaturan akun         | Change password, logout, about                           |
| 19  | Change Password       | Ganti password          | Current, new, confirm password                           |

### Status Badges (Color)

| Status                  | Color    | Meaning                   |
| ----------------------- | -------- | ------------------------- |
| SUBMITTED               | 🔵 Blue  | Menunggu Marketing        |
| MARKETING_APPROVED      | 🔵 Blue  | Menunggu Branch Manager   |
| BRANCH_MANAGER_APPROVED | 🔵 Blue  | Menunggu Backoffice       |
| APPROVED                | 🟢 Green | Disetujui, dana dicairkan |
| MARKETING_REJECTED      | 🔴 Red   | Ditolak Marketing         |
| BRANCH_MANAGER_REJECTED | 🔴 Red   | Ditolak Branch Manager    |
| REJECTED                | 🔴 Red   | Ditolak Backoffice        |

---

## 3. Web Dashboard (Internal)

### Role-Based Screens

#### Marketing & Branch Manager

| Screen                | Deskripsi                                        |
| --------------------- | ------------------------------------------------ |
| Login                 | Email, password                                  |
| Dashboard             | Pending loans count, branch info                 |
| Pending Loans         | Table dengan filter, search, pagination          |
| Loan Detail           | Customer info, loan info, approve/reject buttons |
| Approval Confirmation | Note input, confirm modal                        |

#### Backoffice

| Screen        | Deskripsi                    |
| ------------- | ---------------------------- |
| Dashboard     | All branches pending count   |
| Pending Loans | Semua cabang, final approval |
| Reports       | (Optional) Loan statistics   |

#### SuperAdmin

| Screen            | Deskripsi                              |
| ----------------- | -------------------------------------- |
| Dashboard         | System overview                        |
| User Management   | List users, create, edit, assign roles |
| Create User Form  | Name, email, password, role, branch    |
| Role Management   | List roles, edit permissions           |
| Permission Matrix | Role vs Permission checkboxes          |

---

## 4. Design System

### Color Palette

```
Primary:
  - Gradient: #667eea → #764ba2 (Purple-Violet)
  - Primary: #667eea
  - Primary Dark: #5a67d8

Status:
  - Success: #48BB78 (Green)
  - Warning: #ECC94B (Yellow)
  - Error: #F56565 (Red)
  - Info: #4299E1 (Blue)

Neutral:
  - Background: #F7FAFC
  - Card: #FFFFFF
  - Text Primary: #1A202C
  - Text Secondary: #718096
  - Border: #E2E8F0
```

### Typography

```
Mobile (Android):
  - Font: Roboto / Inter
  - H1: 24sp Bold
  - H2: 20sp SemiBold
  - Body: 14sp Regular
  - Caption: 12sp Regular

Web:
  - Font: Inter / Segoe UI
  - H1: 32px Bold
  - H2: 24px SemiBold
  - Body: 14px Regular
  - Caption: 12px Regular
```

### Components

| Component    | Style                                     |
| ------------ | ----------------------------------------- |
| Cards        | Rounded 12px, shadow, white bg            |
| Buttons      | Rounded 8px, gradient primary, white text |
| Input Fields | Rounded 8px, border, focus state          |
| Status Badge | Pill shape, colored bg, white/dark text   |
| Progress Bar | Rounded, gradient fill                    |
| Timeline     | Vertical stepper, colored dots            |

---

## 5. Sample Prompts

### Prompt 1: Android Splash & Auth Screens

```
Create a HiFi mobile prototype for a loan application Android app called "EHEFIN".

Design the following screens:
1. Splash Screen - Logo centered, gradient background (#667eea to #764ba2)
2. Login Screen - Email & password fields, "Forgot Password" link, "Register" button
3. Register Screen - Name, email, password fields with validation hints
4. Forgot Password - Email input, "Send Reset Link" button

Design style:
- Modern, clean, professional banking app
- Primary gradient: #667eea → #764ba2
- Font: Inter or Roboto
- Rounded corners (12px for cards, 8px for buttons)
- White cards with subtle shadows

Include proper spacing, status bar, and navigation elements.
```

### Prompt 2: Customer Home & Profile

```
Create HiFi mobile screens for EHEFIN loan app - Customer Dashboard:

1. Home Dashboard:
   - Header with user greeting and profile avatar
   - Active Plafond Card showing:
     * Product tier (BRONZE/SILVER/GOLD/PLATINUM)
     * Remaining amount / Original amount
     * Progress bar
   - Recent Loans section (list of 2-3 items with status badges)
   - Quick Action buttons: "Apply Loan", "View Profile"

2. Profile Screen:
   - Profile photo placeholder
   - User info: Name, Email
   - Profile completeness indicator (progress ring)
   - Fields: NIK, Birthdate, Phone, Address
   - "Edit Profile" button

3. Edit Profile Form:
   - Form fields with validation
   - NIK (16 digits), Date picker, Phone, Address textarea
   - "Save" button

Color scheme: Purple gradient theme (#667eea)
Style: Clean, modern banking UI
```

### Prompt 3: Loan Application Flow

```
Create HiFi mobile screens for loan application flow:

1. Product Selection:
   - Grid of 4 product cards: BRONZE, SILVER, GOLD, PLATINUM
   - Each card shows: Name, Max Amount (Rp), Max Tenor, Interest Rate
   - "Select" button on each card

2. Loan Application Form:
   - Selected product summary at top
   - Amount input (slider or number input)
   - Tenor dropdown (months)
   - Interest rate input
   - Branch selector dropdown
   - Remaining plafond info
   - "Submit" button

3. Confirmation Screen:
   - Summary of all loan details
   - Terms and conditions checkbox
   - "Confirm & Submit" button

4. Success Screen:
   - Checkmark animation placeholder
   - "Loan Submitted Successfully" message
   - "Track Status" and "Back to Home" buttons

Design: Modern fintech style, purple gradient accents
```

### Prompt 4: Internal Dashboard - Approval

```
Create HiFi web dashboard for loan approval system:

1. Login Page:
   - Centered card layout
   - Email, password inputs
   - "Login" button with gradient
   - Dark header with logo

2. Dashboard:
   - Sidebar navigation (Dashboard, Pending Loans, Profile)
   - Header with user info and logout
   - Main content:
     * "Pending Approvals" count card
     * Branch info (for Marketing/BM roles)
     * Recent activity table

3. Pending Loans Table:
   - Columns: ID, Customer Name, Amount, Status, Date, Actions
   - Filter by status, search by name
   - Pagination
   - "View" button on each row

4. Loan Detail Page:
   - Left: Customer Info (snapshot data)
   - Right: Loan Details (amount, tenor, rate, branch)
   - Bottom: History Timeline (status progression)
   - Action buttons: "Approve" (green), "Reject" (red)

5. Approval Modal:
   - Note textarea
   - Confirm/Cancel buttons

Design style: Clean enterprise dashboard, purple accent, white cards
```

### Prompt 5: SuperAdmin User Management

```
Create HiFi web screens for SuperAdmin user management:

1. User List:
   - Table: Name, Email, Role, Branch, Status, Actions
   - Add User button
   - Search and filter
   - Edit/Delete actions per row

2. Create/Edit User Form:
   - Modal or side panel
   - Fields: Name, Email, Password, Role dropdown, Branch dropdown
   - Branch required for MARKETING and BRANCH_MANAGER roles
   - Save/Cancel buttons

3. Role Management:
   - List of roles: SUPERADMIN, BACKOFFICE, BRANCH_MANAGER, MARKETING, CUSTOMER
   - Click to edit permissions

4. Permission Matrix:
   - Table: Rows = Permissions, Columns = Roles
   - Checkboxes to assign permissions
   - Save button

Enterprise design, clean tables, purple accent colors
```

---

## Quick Reference

### API Endpoints untuk Prototype Flow

| Flow     | Endpoints                                                                   |
| -------- | --------------------------------------------------------------------------- |
| Auth     | POST /api/auth/register, /login, /forgot-password, /reset-password, /logout |
| Profile  | GET/PUT /api/customer/profile                                               |
| Plafond  | GET /api/products, GET/POST /api/customer/plafond                           |
| Loan     | GET/POST /api/loans, GET /api/loans/{id}, GET /api/loans/{id}/history       |
| Approval | GET /api/approval/pending, POST /api/approval/{id}/approve, /reject         |
| Admin    | GET/POST /api/admin/users, /roles, /permissions                             |

### Key Business Rules untuk Prototype

1. **Customer** harus lengkapi profile sebelum submit loan
2. **Plafond** harus dipilih dulu sebelum apply loan
3. **Pending loan** tidak boleh ada sebelum submit baru
4. **Marketing & BM** hanya lihat cabang sendiri
5. **Backoffice** lihat semua cabang

---

_Documentation generated: 2026-01-01_

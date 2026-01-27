# Frontend Strategy: Visual Prototype

## 1. Philosophy: "Prototype First"
The goal is to build a high-fidelity visual prototype in React **without a backend**. This allows us to validate the User Experience (UX) and User Interface (UI) before investing in complex backend logic (OCR, PDF Parsing).

## 2. Addressing Key Questions
### A. Is a Mobile Version possible?
**YES.**
- **Strategy**: We will use a **Mobile-First Responsive Design**.
- **Technical approach**:
    - Use CSS media queries (via Tailwind standard breakpoints `sm`, `md`, `lg`) to ensure layouts adapt.
    - **Desktop**: Split screen (PDF on left, Form on right).
    - **Mobile**: Stacked layout (Upload -> Process -> Review Card which expands).
    - **PWA (Progressive Web App)**: Later, we can add a `manifest.json` to allow users to "install" the website on their home screen like a native app.

### B. How to make "Specific Visuals" (Not Standard Components)?
To avoid the "Generic Bootstrap/Material" look, we will **not** use a pre-styled component library like MUI or AntDesign for the core visuals.
- **Styling Engine**: **Tailwind CSS**. It gives low-level control over spacing, colors, and typography, allowing us to build a unique "Design System" from scratch very quickly.
- **Animations**: **Framer Motion**. To add the "premium" feel (smooth transitions, entry animations, micro-interactions).
- **Structure**: We will build our own "atomic" components (`<Card>`, `<Button>`, `<Input>`) that strictly follow a custom Art Direction (e.g., Glassmorphism, specific rounded corners, distinct color palette).

## 3. Recommended Stack
- **Framework**: React 19 (via Vite).
- **Language**: TypeScript.
- **Styling**: Tailwind CSS + `clsx` + `tailwind-merge`.
- **Motion**: Framer Motion.
- **Icons**: Lucide React (Clean, consistent SVG icons).
- **PDF Handling (Frontend)**: `react-dropzone` (for drag-n-drop) + `react-pdf` (optional, just for previewing the file).

## 4. The PDF Parsing "Trick" (Mocking)
Since we don't have the backend yet, we will **Mock** the parsing process.
1.  **User Action**: User drops "my_dividend.pdf".
2.  **System Action**: 
    - Display a "Scanning..." animation (2 seconds).
    - **Mock Logic**: Instead of reading the real file, the app loads a **pre-defined JSON object** (e.g., "Total Dividend: 1200 CHF, Tax: 180 CHF") which matches a specific test case.
3.  **Result**: The user sees the form pre-filled with this data.
    - *Benefit*: We can design the "Verification Screen" perfectly without waiting for the OCR to be built.

## 5. Next Steps
1.  Initialize Vite Project.
2.  Set up the "Theme" (Colors, Fonts).
3.  Build the "Upload Hero Section".
4.  Build the "Mock Result Dashboard".

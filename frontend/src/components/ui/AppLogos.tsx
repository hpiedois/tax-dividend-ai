import type { SVGProps } from "react";

// --- Adaptive Elegant Concepts ---

// 1. "Yield Cycle": Adaptive
export const LogoFinReclaim = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        <rect width="40" height="40" rx="12" className="fill-white dark:fill-slate-950 transition-colors" />
        <circle cx="20" cy="20" r="12" strokeWidth="1.5" strokeLinecap="round" className="stroke-slate-200 dark:stroke-slate-800 transition-colors" />
        <path d="M24 16L16 24" strokeWidth="1.5" strokeLinecap="round" className="stroke-slate-900 dark:stroke-white transition-colors" />
        <path d="M24 16H20M24 16V20" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="stroke-slate-900 dark:stroke-white transition-colors" />
        <circle cx="17" cy="17" r="1.5" className="fill-brand-500" />
        <circle cx="23" cy="23" r="1.5" className="fill-brand-500" />
    </svg>
);

// 2. "Paper Graph": Adaptive - Updated to "Dividend Statement" (Doc + Graph)
export const LogoFinDoc = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        <rect width="40" height="40" rx="12" className="fill-white dark:fill-slate-950 transition-colors" />

        {/* Document Path with Folded Corner */}
        <path
            d="M12 10C12 8.89543 12.8954 8 14 8H24L30 14V30C30 31.1046 29.1046 32 28 32H14C12.8954 32 12 31.1046 12 30V10Z"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="stroke-slate-300 dark:stroke-slate-700 fill-slate-50 dark:fill-white/5 transition-colors"
        />
        <path d="M24 8V14H30" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="stroke-slate-300 dark:stroke-slate-700" />

        {/* Statement Lines (Rows) - Subtle background for graph */}
        <path d="M16 16H22" strokeWidth="1.5" strokeLinecap="round" className="stroke-slate-200 dark:stroke-slate-700" />
        <path d="M16 20H26" strokeWidth="1.5" strokeLinecap="round" className="stroke-slate-200 dark:stroke-slate-700" />
        <path d="M16 24H24" strokeWidth="1.5" strokeLinecap="round" className="stroke-slate-200 dark:stroke-slate-700" />

        {/* Rising Graph Overlay - The "Dividend Growth" */}
        <path d="M10 28L18 22L22 25L30 18" className="stroke-brand-500 dark:stroke-brand-400 transition-colors" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        <path d="M30 18V22M30 18H26" className="stroke-brand-500 dark:stroke-brand-400 transition-colors" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
);

// 3. "Dividends": Adaptive
export const LogoFinYield = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        <rect width="40" height="40" rx="12" className="fill-white dark:fill-slate-950 transition-colors" />
        <ellipse cx="14" cy="28" rx="5" ry="2.5" strokeWidth="1.5" className="stroke-slate-400 dark:stroke-slate-600 transition-colors" />
        <path d="M9 28V26C9 24.5 11.5 24 14 24C16.5 24 19 24.5 19 26V28" strokeWidth="1.5" className="stroke-slate-400 dark:stroke-slate-600 transition-colors" />
        <ellipse cx="20" cy="22" rx="5" ry="2.5" strokeWidth="1.5" className="stroke-slate-600 dark:stroke-slate-400 transition-colors" />
        <path d="M15 22V20C15 18.5 17.5 18 20 18C22.5 18 25 18.5 25 20V22" strokeWidth="1.5" className="stroke-slate-600 dark:stroke-slate-400 transition-colors" />
        <ellipse cx="26" cy="16" rx="5" ry="2.5" strokeWidth="1.5" className="stroke-brand-500 fill-brand-50 dark:fill-brand-900/50 transition-colors" />
        <path d="M21 16V14C21 12.5 23.5 12 26 12C28.5 12 31 12.5 31 14V16" strokeWidth="1.5" className="stroke-brand-500 transition-colors" />
    </svg>
);

// 4. "Cycle Book" (User Prop 1): Circular arrows + Book - Standardized Geometry
export const LogoFinCycleBook = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        {/* Ring Geometry from LogoFinFinal (Radius 145, Stroke 28) */}
        <path
            d="M392 206 A145 145 0 0 1 206 392"
            fill="none"
            strokeWidth="28"
            strokeLinecap="round"
            className="stroke-slate-300 dark:stroke-slate-600 transition-colors"
        />
        <path
            d="M0 0 L-32 -20 L-32 20 Z"
            transform="translate(173, 375) rotate(215)"
            className="fill-slate-300 dark:fill-slate-600 transition-colors"
        />
        <path
            d="M119 305 A145 145 0 0 1 305 119"
            fill="none"
            strokeWidth="28"
            strokeLinecap="round"
            className="stroke-brand-500 dark:stroke-brand-400 transition-colors"
        />
        <path
            d="M0 0 L-32 -20 L-32 20 Z"
            transform="translate(339, 137) rotate(35)"
            className="fill-brand-500 dark:fill-brand-400 transition-colors"
        />

        {/* Central Content: Book + Arrow (Scaled from original 24px prop) */}
        {/* Original Book was ~10x10 at center. We scale x20 to ~200x200. */}
        <g transform="translate(256, 256) scale(20) translate(-12, -12)">
            {/* Book Body */}
            <rect x="7" y="7" width="5" height="10" rx="1" fill="none" strokeWidth="1.5" className="stroke-brand-700 dark:stroke-slate-200 transition-colors" />
            <rect x="12" y="7" width="5" height="10" rx="1" fill="none" strokeWidth="1.5" className="stroke-brand-700 dark:stroke-slate-200 transition-colors" />

            {/* Inner Arrow */}
            <path d="M9 14l2-2 2 1.5 3-3" fill="none" strokeWidth="1.5" strokeLinecap="round" className="stroke-brand-400 dark:stroke-brand-300 transition-colors" />
        </g>
    </svg>
);

// 5. "Cycle Book Bold" (User Prop 2): Bolder stylistic version - Standardized Geometry
export const LogoFinCycleBookBold = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        {/* Ring Geometry from LogoFinFinal but BOLD (Stroke 36) */}
        <path
            d="M392 206 A145 145 0 0 1 206 392"
            fill="none"
            strokeWidth="36"
            strokeLinecap="round"
            className="stroke-slate-300 dark:stroke-slate-600 transition-colors"
        />
        <path
            d="M0 0 L-40 -24 L-40 24 Z"
            transform="translate(173, 375) rotate(215)"
            className="fill-slate-300 dark:fill-slate-600 transition-colors"
        />
        <path
            d="M119 305 A145 145 0 0 1 305 119"
            fill="none"
            strokeWidth="36"
            strokeLinecap="round"
            className="stroke-brand-500 dark:stroke-brand-400 transition-colors"
        />
        <path
            d="M0 0 L-40 -24 L-40 24 Z"
            transform="translate(339, 137) rotate(35)"
            className="fill-brand-500 dark:fill-brand-400 transition-colors"
        />

        {/* Central Content: Bold Book (Original 512px coords) */}
        {/* Book Body */}
        <path d="M150 170h100c30 0 55 25 55 55v120h-100c-30 0-55-25-55-55z" fill="white" strokeWidth="24" className="fill-slate-50 dark:fill-slate-800 stroke-brand-800 dark:stroke-slate-300 transition-colors" />
        <path d="M362 170h-100c-30 0-55 25-55 55v120h100c30 0 55-25 55-55z" fill="white" strokeWidth="24" className="fill-slate-50 dark:fill-slate-800 stroke-brand-800 dark:stroke-slate-300 transition-colors" />

        {/* Inner Arrow */}
        <path d="M190 295l50-50 45 35 65-80" fill="none" strokeWidth="36" strokeLinecap="round" strokeLinejoin="round" className="stroke-brand-500 dark:stroke-brand-400 transition-colors" />
        <polygon points="340,180 375,175 370,210" className="fill-brand-500 dark:fill-brand-400 transition-colors" />
    </svg>
);

// 6. "Final": Cycle + Scaled Doc + Curve (App Icon Style)
export const LogoFinFinal = (props: SVGProps<SVGSVGElement>) => (
    <svg viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
        {/* Gradients */}
        <defs>
            <linearGradient id="iconGrad" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stopColor="#4FC3F7" />
                <stop offset="100%" stopColor="#1A237E" />
            </linearGradient>
        </defs>

        {/* Background Box */}
        <rect x="16" y="16" width="480" height="480" rx="96" fill="url(#iconGrad)" />

        {/* ARC 1 (White) */}
        {/* Radius 145. Start 340. End 110. */}
        <path
            d="M392 206 A145 145 0 0 1 206 392"
            fill="none"
            stroke="white"
            strokeWidth="28"
            strokeLinecap="round"
        />
        {/* Arrow 1: Detached +20 deg -> 130 deg */}
        {/* Coords for 130deg @ 145px: x=163, y=367 */}
        <path
            d="M0 0 L-32 -20 L-32 20 Z"
            transform="translate(163, 367) rotate(220)"
            fill="white"
        />

        {/* ARC 2 (Light Blue) */}
        {/* Radius 145. Start 160. End 290. */}
        <path
            d="M119 305 A145 145 0 0 1 305 119"
            fill="none"
            stroke="#E3F2FD"
            strokeWidth="28"
            strokeLinecap="round"
        />
        {/* Arrow 2: Detached +20 deg -> 310 deg */}
        {/* Coords for 310deg @ 145px: x=349, y=145 */}
        <path
            d="M0 0 L-32 -20 L-32 20 Z"
            transform="translate(349, 145) rotate(40)"
            fill="#E3F2FD"
        />

        {/* CENTRAL GROUP (Scaled) */}
        <g transform="translate(256, 256) scale(1.35) translate(-256, -256)">
            {/* Doc (White) */}
            <rect x="196" y="188" width="120" height="136" rx="14" fill="white" />

            {/* Header (Dark Blue) */}
            <rect x="214" y="206" width="84" height="12" rx="6" fill="#1A237E" />

            {/* Rows (Light Blue) */}
            <rect x="214" y="232" width="64" height="8" rx="4" fill="#90CAF9" />
            <rect x="214" y="252" width="84" height="8" rx="4" fill="#90CAF9" />
            <rect x="214" y="272" width="56" height="8" rx="4" fill="#90CAF9" />

            {/* Stock Curve (Dark Blue) */}
            <path
                d="M206 294 L236 264 L256 284 L286 244 L306 224"
                fill="none"
                stroke="#1A237E"
                strokeWidth="12"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </g>
    </svg>
);

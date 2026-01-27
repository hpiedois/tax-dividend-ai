import { motion, type HTMLMotionProps } from 'framer-motion';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface CardProps extends HTMLMotionProps<"div"> {
    variant?: 'static' | 'interactive';
}

export function Card({
    className,
    variant = 'static',
    children,
    ...props
}: CardProps) {

    const isInteractive = variant === 'interactive';

    return (
        <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            whileHover={isInteractive ? { y: -4, boxShadow: "0 20px 40px -5px rgba(0, 0, 0, 0.1)" } : undefined}
            className={cn(
                "bg-card text-card-foreground border border-border rounded-lg shadow-sm p-6",
                isInteractive && "cursor-pointer transition-shadow duration-300",
                className
            )}
            {...props}
        >
            {children}
        </motion.div>
    );
}

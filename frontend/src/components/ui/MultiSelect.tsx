import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Check, ChevronDown, X } from 'lucide-react';


// If cn utility doesn't exist, I'll inline a simple version or you can tell me. 
// For now I'll assume standard Shadcn-like structure or just use template literals if simple.
// I will implement a local 'cn' helper inside this file to be safe if 'lib/utils' is not confirmed, 
// but usually it is there. I'll check previous files... 'clsx' and 'tailwind-merge' are in package.json.
// I'll assume 'src/lib/utils.ts' exists or I'll simple use standard string concat for now to be safe.

import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export interface Option {
    label: string;
    value: string;
}

interface MultiSelectProps {
    options: Option[];
    selected: string[];
    onChange: (selected: string[]) => void;
    placeholder?: string;
    className?: string;
}

export function MultiSelect({ options, selected, onChange, placeholder = "Select...", className }: MultiSelectProps) {
    const [isOpen, setIsOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    // Close on click outside
    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleSelect = (value: string) => {
        if (selected.includes(value)) {
            onChange(selected.filter((item) => item !== value));
        } else {
            onChange([...selected, value]);
        }
    };

    const handleClear = (e: React.MouseEvent) => {
        e.stopPropagation();
        onChange([]);
    };

    const getDisplayLabel = () => {
        if (selected.length === 0) return <span className="text-muted-foreground">{placeholder}</span>;
        if (selected.length === 1) {
            const option = options.find(o => o.value === selected[0]);
            return <span className="text-foreground">{option?.label}</span>;
        }
        return <span className="text-foreground">{selected.length} selected</span>;
    };

    return (
        <div className={cn("relative min-w-[180px]", className)} ref={containerRef}>
            <div
                className={cn(
                    "flex items-center justify-between w-full px-3 py-2 bg-background border border-border rounded-lg text-sm cursor-pointer hover:border-primary/50 transition-colors",
                    isOpen && "ring-2 ring-primary/20 border-primary"
                )}
                onClick={() => setIsOpen(!isOpen)}
            >
                <div className="flex-1 truncate mr-2">
                    {getDisplayLabel()}
                </div>
                <div className="flex items-center gap-1">
                    {selected.length > 0 && (
                        <div
                            onClick={handleClear}
                            className="p-0.5 rounded-full hover:bg-muted text-muted-foreground hover:text-foreground transition-colors"
                        >
                            <X className="w-3 h-3" />
                        </div>
                    )}
                    <ChevronDown className={cn("w-4 h-4 text-muted-foreground transition-transform", isOpen && "rotate-180")} />
                </div>
            </div>

            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, y: 5, scale: 0.95 }}
                        animate={{ opacity: 1, y: 0, scale: 1 }}
                        exit={{ opacity: 0, y: 5, scale: 0.95 }}
                        transition={{ duration: 0.1 }}
                        className="absolute top-full left-0 right-0 mt-1 z-50 bg-popover border border-border rounded-lg shadow-lg overflow-hidden"
                    >
                        <div className="max-h-[200px] overflow-y-auto p-1 text-sm">
                            {options.length === 0 ? (
                                <div className="p-2 text-center text-muted-foreground text-xs">No options</div>
                            ) : (
                                <div className="space-y-0.5">
                                    {options.map((option) => {
                                        const isSelected = selected.includes(option.value);
                                        return (
                                            <div
                                                key={option.value}
                                                className={cn(
                                                    "flex items-center gap-2 px-2 py-1.5 rounded-md cursor-pointer transition-colors",
                                                    isSelected ? "bg-primary/10 text-primary font-medium" : "hover:bg-muted text-foreground"
                                                )}
                                                onClick={() => handleSelect(option.value)}
                                            >
                                                <div className={cn(
                                                    "w-4 h-4 rounded border flex items-center justify-center transition-colors",
                                                    isSelected ? "bg-primary border-primary" : "border-muted-foreground"
                                                )}>
                                                    {isSelected && <Check className="w-3 h-3 text-primary-foreground" />}
                                                </div>
                                                <span>{option.label}</span>
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}

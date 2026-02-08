import { useState } from 'react';
import { Settings2 } from 'lucide-react';
import { SCENARIOS } from '../../mocks/scenarios';
import { mockState } from '../../api/mock-state';
import { useQueryClient } from '@tanstack/react-query';

export function MockSelector() {
    const [isOpen, setIsOpen] = useState(false);
    const [currentScenario, setCurrentScenario] = useState(mockState.activeScenarioId);
    const queryClient = useQueryClient();

    const handleSelect = (scenarioId: string) => {
        mockState.setScenario(scenarioId);
        setCurrentScenario(scenarioId);
        setIsOpen(false);
        // Invalidate all queries to refresh UI with new mock data
        queryClient.invalidateQueries();
    };

    return (
        <div className="fixed bottom-4 right-4 z-[9999] flex flex-col items-end gap-2">
            {isOpen && (
                <div className="bg-white rounded-lg shadow-lg border border-slate-200 p-2 mb-2 w-48 animate-in fade-in slide-in-from-bottom-2">
                    <p className="text-xs font-semibold text-slate-500 mb-2 px-2 uppercase tracking-wider">Mock Data</p>
                    <div className="space-y-1">
                        {Object.values(SCENARIOS).map((scenario) => (
                            <button
                                key={scenario.id}
                                onClick={() => handleSelect(scenario.id)}
                                className={`w-full text-left px-3 py-2 text-sm rounded-md transition-colors ${currentScenario === scenario.id
                                    ? 'bg-brand-50 text-brand-700 font-medium'
                                    : 'hover:bg-slate-50 text-slate-700'
                                    }`}
                            >
                                {scenario.label}
                            </button>
                        ))}
                    </div>
                </div>
            )}

            <button
                onClick={() => setIsOpen(!isOpen)}
                className={`flex items-center gap-2 px-4 py-2 rounded-full shadow-lg border transition-all ${isOpen
                    ? 'bg-slate-900 text-white border-slate-900'
                    : 'bg-white text-slate-600 border-slate-200 hover:border-brand-300 hover:text-brand-600'
                    }`}
            >
                <Settings2 className="w-4 h-4" />
                <span className="text-sm font-medium">Debug Mocks</span>
            </button>
        </div>
    );
}

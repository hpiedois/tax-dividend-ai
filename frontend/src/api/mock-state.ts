import { SCENARIOS } from '../mocks/scenarios';

// simple event emitter for scenario changes
class MockStateManager extends EventTarget {
    private currentScenarioId: string = 'few'; // Default

    get activeScenario() {
        return SCENARIOS[this.currentScenarioId] || SCENARIOS.few;
    }

    get activeScenarioId() {
        return this.currentScenarioId;
    }

    setScenario(id: string) {
        if (SCENARIOS[id]) {
            this.currentScenarioId = id;
            this.dispatchEvent(new CustomEvent('change', { detail: id }));
            console.log('[MockState] Switched to scenario:', id);
            // Optionally force reload or invalidate queries here if we had access to queryClient
            // For now, a simple page reload might be easiest for the user, or let react-query refetch
        }
    }
}

export const mockState = new MockStateManager();

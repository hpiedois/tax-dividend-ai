#!/bin/bash

# ============================================================================
# Run Tests - Tax Dividend AI Backend
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

TEST_TYPE=${1:-all}

echo "🧪 Running Tests - Tax Dividend AI Backend"
echo "==========================================="
echo ""

case $TEST_TYPE in
  unit)
    echo "Running unit tests only..."
    mvn test
    ;;

  integration)
    echo "Running integration tests (with Testcontainers)..."
    mvn verify -DskipUnitTests
    ;;

  all)
    echo "Running all tests..."
    mvn verify
    ;;

  *)
    echo "❌ Error: Invalid test type '$TEST_TYPE'"
    echo "Usage: ./run-tests.sh [unit|integration|all]"
    echo ""
    echo "Examples:"
    echo "  ./run-tests.sh unit         # Run unit tests only"
    echo "  ./run-tests.sh integration  # Run integration tests only"
    echo "  ./run-tests.sh all          # Run all tests (default)"
    exit 1
    ;;
esac

echo ""
if [ $? -eq 0 ]; then
    echo "✅ All tests passed!"
else
    echo "❌ Some tests failed. Check output above."
    exit 1
fi

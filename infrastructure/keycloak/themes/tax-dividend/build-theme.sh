#!/bin/bash

# Script to build the Keycloak theme CSS

set -e

echo "🎨 Building Keycloak theme CSS..."

# Check if we're in the theme directory
if [ ! -f "package.json" ]; then
    echo "❌ Error: Not in theme directory"
    exit 1
fi

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Build CSS
echo "⚙️  Compiling Tailwind CSS..."
npm run build

echo "✅ Theme CSS built successfully!"
echo "📁 Output: login/resources/css/output.css"

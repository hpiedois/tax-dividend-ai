# Tax Dividend AI - Keycloak Theme

Custom Keycloak theme matching the Tax Dividend AI frontend design.

## 🎨 Features

- ✨ Modern gradient background (light & dark mode)
- 🌓 Automatic dark mode detection
- 🎯 Tax Dividend AI logo
- 🔵 Brand colors (sky blue)
- 📱 Responsive design
- ✨ Glass-morphism effects

## 📁 Structure

```
tax-dividend/
├── login/                      # Login theme
│   ├── theme.properties        # Theme configuration
│   ├── template.ftl            # Main layout template
│   ├── login.ftl              # Login page
│   ├── register.ftl           # Registration page
│   └── resources/
│       ├── css/
│       │   ├── input.css      # Tailwind source
│       │   └── output.css     # Compiled CSS
│       └── img/
│           └── logo.svg       # Tax Dividend AI logo
├── package.json               # Build configuration
├── tailwind.config.js         # Tailwind configuration
└── build-theme.sh            # CSS build script
```

## 🔧 Development

### Build CSS

After modifying Tailwind classes in `.ftl` templates:

```bash
cd infrastructure/keycloak/themes/tax-dividend
npm run build
```

### Apply Theme

After infrastructure reset:

```bash
make apply-theme
```

Or manually:

```bash
./infrastructure/keycloak/apply-theme.sh
```

### Test Changes

1. Rebuild CSS: `npm run build`
2. Restart Keycloak: `docker restart tax-dividend-keycloak`
3. Clear browser cache (Ctrl+Shift+R)
4. Go to http://localhost:5173 → Login

## 🎨 Customization

### Colors

Edit `tailwind.config.js` to change brand colors:

```javascript
colors: {
    brand: {
        50: '#f0f9ff',
        // ... your colors
        900: '#0c4a6e',
    }
}
```

### Logo

Replace `login/resources/img/logo.svg` with your logo.

### Layout

Modify `login/template.ftl` for structural changes.

### Forms

Modify `login/login.ftl` or `login/register.ftl` for form styling.

## 🔄 After Infrastructure Reset

The theme files are persistent (in Git), but the realm configuration in the database is reset.

**Automatic solution:**

```bash
make apply-theme
```

**Manual solution:**

```bash
docker exec tax-dividend-keycloak-db psql -U keycloak -d keycloak -c \
  "UPDATE realm SET login_theme = 'tax-dividend' WHERE name = 'tax-dividend';"
```

## 🐛 Troubleshooting

### Theme not appearing

1. Check if theme is applied:
   ```bash
   make apply-theme
   ```

2. Verify theme files are mounted:
   ```bash
   docker exec tax-dividend-keycloak ls -la /opt/keycloak/themes/tax-dividend/
   ```

3. Check Keycloak logs:
   ```bash
   docker logs tax-dividend-keycloak | grep -i theme
   ```

### Dark mode not working

Add this to your browser console:

```javascript
// Force dark mode
localStorage.theme = 'dark'

// Force light mode
localStorage.theme = 'light'

// Auto (system preference)
localStorage.removeItem('theme')
```

Then reload the page.

## 📚 Resources

- [Keycloak Themes Documentation](https://www.keycloak.org/docs/latest/server_development/#_themes)
- [FreeMarker Template Language](https://freemarker.apache.org/)
- [Tailwind CSS](https://tailwindcss.com/)

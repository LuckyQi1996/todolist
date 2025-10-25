# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a minimalist, browser-based todo list application built with Vue 2.x that requires no installation or login. It's a static web application that stores all data locally in the browser's localStorage.

## Key Architecture

### Frontend Structure
- **Single Page Application**: Two HTML files (`index.html` for English, `index-zh.html` for Chinese)
- **Vue 2.x**: Used via CDN (local copy in `public/js/vue.js`) for reactive components
- **Vanilla JavaScript**: No build tools or bundlers required
- **Sass/SCSS**: Styling with compiled CSS files in `public/css/`

### Core Components
- **Main App**: Vue instance managing todos, filters, and UI state
- **Local Storage**: Persists todos, settings, and user preferences
- **Drag & Drop**: Custom implementation for reordering todo items
- **Import/Export**: JSON-based data persistence

### Data Flow
- **LocalStorage**: Primary data store using `uiineed-todos` key
- **Vue Reactivity**: Automatic UI updates when data changes
- **Filter System**: View todos by status (all, in-progress, completed, trash)

## Development Workflow

### Testing the Application
```bash
# Simply open the HTML files in a browser
# No build process required - it's a static site
open index.html        # English version
open index-zh.html     # Chinese version
```

### Styling Development
```bash
# If you need to recompile SCSS (though compiled CSS is already present)
# Requires Sass compiler
sass public/css/style.scss public/css/style.css
```

### File Structure
- `index.html` / `index-zh.html` - Main application files
- `public/css/` - Stylesheets (SCSS source and compiled CSS)
- `public/img/` - Images and icons (including social media icons)
- `public/js/vue.js` - Vue 2.x library (local copy)

## Key Features Implementation

### Todo Management
- **Add Todos**: Enter key submission with validation
- **Edit Todos**: Double-click to edit inline
- **Complete/Uncomplete**: Toggle completion status
- **Delete/Restore**: Soft delete with recycle bin functionality
- **Drag Reorder**: Custom drag-and-drop implementation

### Data Persistence
- **LocalStorage**: All todos stored locally with key `uiineed-todos`
- **Export**: Download todos as JSON/timestamped text file
- **Import**: Append todos from JSON/txt files
- **Settings**: Language preference and custom slogan storage

### UI/UX Features
- **Responsive Design**: Mobile-first approach with breakpoints
- **Animations**: CSS transitions and Vue transition groups
- **Custom Alerts**: Replaced browser alerts with custom modal dialogs
- **Language Detection**: Auto-detects browser language preference

## Important Implementation Details

### State Management
- No external state management library - uses Vue's built-in reactivity
- Direct localStorage operations wrapped in `todoStorage` object
- Deep watching on todos array for automatic persistence

### Styling Approach
- CSS custom properties for theming
- SCSS mixins for responsive breakpoints
- Base64-encoded SVG icons to reduce HTTP requests
- Custom animations and transitions

### Browser Compatibility
- Uses modern JavaScript features but maintains broad compatibility
- Custom scrollbar styling
- Touch-friendly mobile interface

## Localization
- Bilingual support (English/Chinese)
- Language preference persistence in localStorage
- Separate HTML files for each language
- Auto-language detection on first visit
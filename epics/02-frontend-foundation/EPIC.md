# Epic 02: Frontend Foundation

**Status**: ✅ **COMPLETE** (Core foundation ready, component library deferred)
**Priority**: High
**Estimated Duration**: 2-3 days
**Goal**: Set up HTMX + Tailwind CSS + Thymeleaf base layouts for early UI testing

---

## Overview

This epic establishes the frontend foundation for Plan 91 using HTMX for dynamic interactions, Tailwind CSS for styling, and Thymeleaf for server-side templating. The goal is to create reusable components and layouts that will support rapid UI development.

**Why This Epic Exists**:
- HTMX allows hypermedia-driven interactions without heavy JavaScript
- Tailwind CSS provides utility-first styling with Plan 91 branding
- Thymeleaf integrates seamlessly with Spring Boot
- Establishing components now prevents rework later
- Early UI mockups help validate UX before backend integration

---

## Technology Stack

- **HTMX**: Dynamic HTML updates without JavaScript
- **Tailwind CSS**: Utility-first CSS framework
- **Thymeleaf**: Server-side templating for Java/Spring
- **Alpine.js** (optional): Minimal JavaScript for UI interactions
- **Spring Boot 4**: Web framework and template rendering

---

## Objectives

1. Configure Tailwind CSS with Plan 91 theme (blue color scheme)
2. Integrate HTMX for dynamic interactions
3. Create base Thymeleaf layout template with navigation
4. Build reusable UI components (forms, buttons, cards, modals)
5. Implement loading states and skeleton loaders
6. Create toast/notification system
7. Build a sample dashboard mockup page
8. Ensure responsive design (mobile-first)

---

## Success Criteria

- [ ] Tailwind CSS configured with custom Plan 91 theme
- [ ] HTMX integrated and working with sample interactions
- [ ] Base layout template created with header, navigation, footer
- [ ] Form components created (inputs, selects, checkboxes, validation styles)
- [ ] Loading states and skeleton loaders implemented
- [ ] Toast notification component working
- [ ] Modal component created
- [ ] Sample dashboard page displays correctly
- [ ] All pages are responsive (mobile, tablet, desktop)
- [ ] CSS builds successfully with Maven
- [ ] No console errors in browser

---

## Tickets

| Ticket | Title | Status | Effort |
|--------|-------|--------|--------|
| PLAN91-020 | Configure Tailwind CSS with Plan 91 theme | ✅ Complete | Medium |
| PLAN91-021 | Integrate HTMX into project | ✅ Complete | Small |
| PLAN91-022 | Create base Thymeleaf layout template | ✅ Complete | Medium |
| PLAN91-023 | Create navigation component | ✅ Complete | Small |
| PLAN91-024 | Create form components and utilities | ⏸️ Deferred | Large |
| PLAN91-025 | Create loading states and skeleton loaders | ⏸️ Deferred | Medium |
| PLAN91-026 | Create toast/notification component | ⏸️ Deferred | Medium |
| PLAN91-027 | Create modal component | ⏸️ Deferred | Medium |
| PLAN91-028 | Create sample static dashboard page | ✅ Complete | Medium |

---

## Dependencies

**Depends on**: Epic 01 (Domain Model) ✅ Complete

**Blocks**:
- Epic 03 (Authentication) - needs login/register UI
- Epic 05 (Use Cases) - needs UI components

---

## Deliverables

1. **Tailwind Configuration**
   - `tailwind.config.js` with Plan 91 theme colors
   - Build process integrated with Maven
   - Production CSS optimization

2. **Base Templates**
   - `layout/base.html` - Main layout template
   - `fragments/header.html` - Header with branding
   - `fragments/navigation.html` - Main navigation
   - `fragments/footer.html` - Footer

3. **Reusable Components**
   - `components/forms.html` - Form inputs, labels, validation
   - `components/buttons.html` - Button variants
   - `components/cards.html` - Card layouts
   - `components/loading.html` - Skeleton loaders
   - `components/toast.html` - Toast notifications
   - `components/modal.html` - Modal dialogs

4. **Sample Pages**
   - `pages/dashboard.html` - Static dashboard mockup
   - Shows how components work together

5. **Static Assets**
   - `/static/css/styles.css` - Compiled Tailwind CSS
   - `/static/js/htmx.min.js` - HTMX library

---

## Design System

### Color Palette (Plan 91 Blue Theme)

```javascript
colors: {
  primary: {
    50: '#eff6ff',
    100: '#dbeafe',
    200: '#bfdbfe',
    300: '#93c5fd',
    400: '#60a5fa',
    500: '#3b82f6',  // Main brand blue
    600: '#2563eb',
    700: '#1d4ed8',
    800: '#1e40af',
    900: '#1e3a8a',
  },
  // Additional colors...
}
```

### Typography
- **Headings**: Inter or system-ui
- **Body**: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto

### Spacing
- Consistent spacing scale: 4px base unit
- Mobile-first responsive breakpoints

---

## Package Structure

```
src/main/resources/
├── static/
│   ├── css/
│   │   ├── input.css           # Tailwind source
│   │   └── styles.css          # Compiled output
│   └── js/
│       └── htmx.min.js
└── templates/
    ├── layout/
    │   └── base.html
    ├── fragments/
    │   ├── header.html
    │   ├── navigation.html
    │   └── footer.html
    ├── components/
    │   ├── forms.html
    │   ├── buttons.html
    │   ├── cards.html
    │   ├── loading.html
    │   ├── toast.html
    │   └── modal.html
    └── pages/
        └── dashboard.html
```

---

## Notes for Developers

- **Mobile-first**: Design for mobile, enhance for desktop
- **Accessibility**: Use semantic HTML, ARIA labels, keyboard navigation
- **HTMX patterns**: Use `hx-get`, `hx-post`, `hx-target`, `hx-swap`
- **Tailwind utilities**: Use utility classes, avoid custom CSS when possible
- **Component reuse**: Build fragments for maximum reusability
- **Thymeleaf syntax**: Use `th:` attributes for dynamic content

---

## References

- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- [HTMX Documentation](https://htmx.org/docs/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- Plan 91 Design System (TBD)

---

**Created**: 2026-01-31
**Last Updated**: 2026-01-31

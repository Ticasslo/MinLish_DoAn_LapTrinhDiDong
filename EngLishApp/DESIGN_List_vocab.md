---
name: Academic Modern
colors:
  surface: '#fbf8ff'
  surface-dim: '#d5d7ff'
  surface-bright: '#fbf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f2ff'
  surface-container: '#edecff'
  surface-container-high: '#e6e6ff'
  surface-container-highest: '#e0e0ff'
  on-surface: '#161937'
  on-surface-variant: '#444656'
  inverse-surface: '#2b2e4d'
  inverse-on-surface: '#f1efff'
  outline: '#757688'
  outline-variant: '#c5c5d9'
  surface-tint: '#2848ee'
  primary: '#183ce6'
  on-primary: '#ffffff'
  primary-container: '#3d5afe'
  on-primary-container: '#f1f0ff'
  inverse-primary: '#bbc3ff'
  secondary: '#006b5c'
  on-secondary: '#ffffff'
  secondary-container: '#68fadd'
  on-secondary-container: '#007261'
  tertiary: '#4146bc'
  on-tertiary: '#ffffff'
  tertiary-container: '#5b60d6'
  on-tertiary-container: '#f2efff'
  error: '#EF4444'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dee0ff'
  primary-fixed-dim: '#bbc3ff'
  on-primary-fixed: '#000f5d'
  on-primary-fixed-variant: '#002ccd'
  secondary-fixed: '#68fadd'
  secondary-fixed-dim: '#44ddc1'
  on-secondary-fixed: '#00201a'
  on-secondary-fixed-variant: '#005145'
  tertiary-fixed: '#e1e0ff'
  tertiary-fixed-dim: '#bfc1ff'
  on-tertiary-fixed: '#03006d'
  on-tertiary-fixed-variant: '#3135ac'
  background: '#fbf8ff'
  on-background: '#161937'
  surface-variant: '#e0e0ff'
  primary-dark: '#0031CA'
  surface-light: '#F8F9FF'
  surface-dark: '#0031CA'
  card-light: '#FFFFFF'
  card-dark: '#1A1D3B'
  subtle-text: '#6B7280'
  success: '#10B981'
  warning: '#F59E0B'
  divider: '#E5E7EB'
typography:
  display-word:
    fontFamily: Nunito
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Nunito
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 36px
  headline-md:
    fontFamily: Nunito
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  headline-sm:
    fontFamily: Nunito
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  button:
    fontFamily: Nunito
    fontSize: 15px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.01em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  body-base:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  caption:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-screen: 16px
  section-gap: 32px
  group-gap: 24px
  element-gap: 12px
  base-unit: 4px
---

## Brand & Style

The design system for this intelligent vocabulary learning app is built on a "Clean Academic" philosophy. It balances the rigor of structured learning with the approachability of a modern mobile experience. The brand personality is focused, intelligent, and encouraging, utilizing a "Card-based" architecture to organize complex Spaced Repetition System (SRS) data into digestible modules.

The visual style is **Corporate / Modern** with a high degree of "Softness." It leverages generous whitespace, a structured indigo-based color palette, and significant corner radii to create a friendly, non-intimidating environment for language acquisition. The interface is primarily flat, relying on subtle tonal shifts and precise elevation to guide the user's focus toward the primary learning objective: the flashcard.

## Colors

The color system is anchored by a high-energy **Indigo** (Primary), representing focus and intelligence, paired with a **Teal** (Secondary) accent used for motivational triggers like streaks and progress. 

### Color Application
- **Primary & Primary Dark:** Used for high-priority CTAs and active states.
- **Primary Light:** Utilized for low-contrast backgrounds in chips and badges to maintain brand presence without overwhelming content.
- **Surface & Card:** The system uses a two-tier background model. The `Surface` acts as the global canvas, while `Card` provides the container for interactive content.
- **Semantic Colors:** Directly mapped to SRS feedback loops. `Error` signifies "Again," `Warning` signifies "Hard," and `Success` signifies "Easy/Good."

### Dark Mode
In Dark Mode, the `Surface` shifts to a deep navy (`#0F1130`) to reduce eye strain during night study sessions, with `Card` surfaces lifting slightly in tone (`#1A1D3B`) to maintain depth hierarchy.

## Typography

This system employs a dual-typeface strategy to distinguish between brand expression and functional reading.

- **Nunito (Display/Headings/Buttons):** Chosen for its rounded terminals and friendly, open character. It is used for all "active" UI elements and brand touchpoints.
- **Inter (Body/System):** A highly legible neo-grotesque used for all "informative" text, ensuring that definitions and long-form descriptions are readable at small sizes.

The **Display-Word** level is reserved exclusively for the front-face of learning cards, using an ExtraBold weight to maximize visual impact during memorization.

## Layout & Spacing

The layout follows a **Fixed Grid** logic for mobile devices, anchored by a standard 16px horizontal screen margin. The spacing rhythm is strictly derived from a 4px/8px base unit.

- **Section Separation:** Use 32px to separate distinct functional areas (e.g., the gap between a card image and its title).
- **Component Grouping:** Standard cards and related modules should be separated by 24px.
- **Inner Padding:** Use 12px or 8px for internal element grouping, such as the spacing between a word and its pronunciation hint.

## Elevation & Depth

Visual hierarchy is established through a combination of **Tonal Layers** and **Ambient Shadows**. 

1.  **Level 0 (Surface):** The base background layer (`Surface`).
2.  **Level 1 (Standard Containers):** Dashboard cards and list items use a subtle 2dp shadow (low blur, 10% opacity) to lift them slightly from the surface.
3.  **Level 2 (Active Learning):** The active Flashcard utilizes an 8dp elevation. This creates a high-contrast depth effect, signaling the card as a physical, "playable" object that exists above the rest of the interface.

In Dark Mode, elevation is communicated primarily through surface color lightening (tonal layers), though the 8dp shadow remains as a subtle glow effect to maintain the flashcard's prominence.

## Shapes

The design system uses a hierarchical rounding strategy to communicate "containment."

- **Flashcards (20px):** The most rounded elements, emphasizing their unique status as the core interaction object.
- **Standard Cards (16px):** Used for large containers and dashboard modules.
- **Buttons (12px):** Optimized for touch targets, providing a friendly but distinct interactive shape.
- **Chips & Badges (8px):** The smallest discrete elements, using a tighter radius to maintain clarity at small scales.

## Components

### Buttons
- **Primary:** Filled with `#3D5AFE`, using `Nunito Bold` text. 12px corner radius.
- **SRS Actions:** Color-coded by semantic role (Again: Red, Hard: Orange, Good/Easy: Teal/Green).
- **Ghost/Add:** Dashed border for the "Add new word" placeholder, encouraging user action through a "missing piece" metaphor.

### Flashcards
The centerpiece of the UI.
- **Front:** Center-aligned `Display-Word` typography. 20px radius, 8dp elevation.
- **Transition:** 3D Y-axis rotation (0° to 180°) with a swap of content at the 90° midpoint.

### Chips & Inputs
- **Chips:** Use `Primary Light` background for active states or `Divider` for neutral states. 8px radius.
- **Input Fields:** Outlined style using `Divider` color for the border. On focus, the border transitions to `Primary` (Indigo).

### Cards & Lists
- **Dashboard Modules:** White (Light Mode) or `#1A1D3B` (Dark Mode) with 16px radius and 2dp elevation. Use `Heading 2` for titles within cards.
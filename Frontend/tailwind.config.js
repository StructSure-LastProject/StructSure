/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    borderRadius: {
      'none': '0',
      'sm': '0.125rem',
      DEFAULT: '0.25rem',
      DEFAULT: '4px',
      'md': '0.375rem',
      'lg': '1rem',
      'full': '9999px',
      'large': '12px',
    },
    extend: {
      inset: {
        '3px': '3px',
      },
      colors: {
        'structsure-black': '#181818',
        'structsure-white': '#ffffff',
        'structsure-dark-grey': '#E9E9EB',
        'structsure-light-grey': '#F2F2F4',
        'structsure-red': '#F13327',
        'structsure-ok': '#25B51F',
        'structsure-defaillant': '#F19327',
        'structsure-unknown': '#6A6A6A',
      },
    },
  },
  plugins: [],
}
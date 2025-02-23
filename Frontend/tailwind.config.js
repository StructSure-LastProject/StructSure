export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
    "./components/*.{js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      fontFamily: {
        poppins: ["Poppins", "sans-serif"], // Add Poppins font
      },
      colors: {
        black: '#181818',
        white: '#FFFFFF',
        lightgray: '#F2F2F4',
        red: '#F13327',
        green: '#25B61F',
        gray: '#6A6A6A',
        orange: '#F19327',
      }
    },
  },
  plugins: [
    require("@tailwindcss/typography"),
  ],
}

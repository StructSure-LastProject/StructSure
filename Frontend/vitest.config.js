import solid from "vite-plugin-solid"
import { defineConfig } from "vitest/config"

/**
 * Vitest config file
 */
export default defineConfig({
  plugins: [solid()],
  test: {
    environment: 'jsdom', 
    globals: true,
    coverage: {
      provider: 'istanbul', 
      reporter: ['text', 'lcov'],
      reportsDirectory: 'coverage',
      include: ['src/**/*.js', 'src/**/*.jsx', 'src/**/*.ts', 'src/**/*.tsx'],
    },
    // Optional: Adjust the test timeout
    testTimeout: 10000,
  },
})
import { render } from '@solidjs/testing-library';
import Home from '../pages/Home';
import { expect, vi } from 'vitest';

// Mocking the Header and StructSure components
vi.mock('../components/Header', () => ({
  default: () => <div data-testid="header">Header Component</div>,
}));

vi.mock('../components/Structure', () => ({
  default: () => <div data-testid="structure">Structure Component</div>,
}));

describe('Home component', () => {
  it('renders Header and StructSure components', () => {
    // Render the Home component
    const { getByTestId } = render(() => <Home />);

    // Check if the Header component is rendered
    expect(getByTestId('header')).toBeInTheDocument();

    // Check if the Structure component is rendered
    expect(getByTestId('structure')).toBeInTheDocument();
  });

  it('should have correct CSS classes in the root div', () => {
    // Render the Home component
    const { container } = render(() => <Home />);

    // Assert that the root div has the expected classes
    expect(container.firstChild).toHaveClass('p-25px');
    expect(container.firstChild).toHaveClass('bg-lightgray');
    expect(container.firstChild).toHaveClass('min-h-screen');
  });
});

import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import App from './App';

describe('App Component', () => {
  it('renders the header correctly', () => {
    render(<App />);
    const heading = screen.getByText(/Live Flight Tracker/i);
    expect(heading).toBeInTheDocument();
  });
});

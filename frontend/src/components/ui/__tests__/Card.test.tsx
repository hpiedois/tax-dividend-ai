import { describe, it, expect } from 'vitest';
import { render, screen } from '../../../test/utils';
import { Card } from '../Card';

describe('Card', () => {
  it('should render children', () => {
    render(
      <Card>
        <p>Card content</p>
      </Card>
    );

    expect(screen.getByText('Card content')).toBeInTheDocument();
  });

  it('should apply default styles', () => {
    const { container } = render(<Card>Content</Card>);
    const card = container.firstChild;

    expect(card).toHaveClass('rounded-lg');
    expect(card).toHaveClass('bg-card');
  });

  it('should merge custom className with defaults', () => {
    const { container } = render(<Card className="custom-class">Content</Card>);
    const card = container.firstChild;

    expect(card).toHaveClass('custom-class');
    expect(card).toHaveClass('rounded-lg'); // Should still have default classes
  });
});

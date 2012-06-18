
package viewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Lays out all components to fit the whole area
 * @author Martin Steiger
 */
public class FillLayout implements LayoutManager
{
	@Override
	public void addLayoutComponent(final String name, final Component component)
	{
		// ignore
	}

	@Override
	public void removeLayoutComponent(Component component)
	{
		// ignore
	}

	@Override
	public Dimension preferredLayoutSize(final Container container)
	{
		return container.getSize();
	}

	@Override
	public Dimension minimumLayoutSize(final Container container)
	{
		return preferredLayoutSize(container);
	}

	@Override
	public void layoutContainer(final Container container)
	{
		int width = container.getWidth();
		int height = container.getHeight();

		for (int i = 0; i < container.getComponentCount(); i++)
		{
			Component component = container.getComponent(i);
			component.setBounds(0, 0, width, height);
		}
	}
}

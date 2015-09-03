
package main;

/**
 * Represents a range
 * @param <T> the type of elements 
 * @author Martin Steiger
 */
public class Range<T>
{
	private T start;
	private T end;
	
	/**
	 * @param start the start value
	 * @param end the end value
	 */
	public Range(T start, T end)
	{
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	public T getStart()
	{
		return start;
	}

	/**
	 * @return the end
	 */
	public T getEnd()
	{
		return end;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Range<?> other = (Range<?>) obj;
		if (end == null)
		{
			if (other.end != null)
				return false;
		}
		else if (!end.equals(other.end))
			return false;
		
		if (start == null)
		{
			if (other.start != null)
				return false;
		}
		else if (!start.equals(other.start))
			return false;
		
		return true;
	}

}

import java.util.*;
import java.awt.*;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class IMECEPathFinder{
	  public int[][] grid;
	  public int height, width;
	  public int maxFlyingHeight;
	  public double fuelCostPerUnit, climbingCostPerUnit;

	  public IMECEPathFinder(String filename, int rows, int cols, int maxFlyingHeight, double fuelCostPerUnit, double climbingCostPerUnit){

		  grid = new int[rows][cols];
		  this.height = rows;
		  this.width = cols;
		  this.maxFlyingHeight = maxFlyingHeight;
		  this.fuelCostPerUnit = fuelCostPerUnit;
		  this.climbingCostPerUnit = climbingCostPerUnit;

		  try
		  {
			Scanner scanner = new Scanner(new File(filename));
			for(int row = 0; row < rows; row++)
		  	{
				for(int column = 0; column < cols; column++)
				{
					int elevation = scanner.nextInt();
					grid[row][column] = elevation;
				}
		  	}
		  }
		  catch(FileNotFoundException e)
		  {
			e.printStackTrace();
		  }
	  }

	  /**
	   * Draws the grid using the given Graphics object.
	   * Colors should be grayscale values 0-255, scaled based on min/max elevation values in the grid
	   */
	  public void drawGrayscaleMap(Graphics g){

		int maxElevation = -1;
		int minElevation = Integer.MAX_VALUE;
		for(int row = 0; row < height; row++)
		{
			for(int column = 0; column < width; column++)
			{
				int elevation = grid[row][column];
				if(elevation < minElevation)
				{
					minElevation = elevation;
				}
				if(elevation > maxElevation)
				{
					maxElevation = elevation;
				}
			}
		}
		double interval = (maxElevation - minElevation) / 255.0;
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++) 
			{
				int value = (int)((grid[i][j] - minElevation) / interval);
				g.setColor(new Color(value, value, value));
				g.fillRect(j, i, 1, 1);
			}
		}
	  }
	
	//Creates and Fills grayscaleMap.dat file.  	
	public void createGrayscaleMapFile()
	{
		try
		{
			File file = new File("grayscaleMap.dat");
			FileWriter writer = new FileWriter(file);
			
			int maxElevation = -1;
			int minElevation = Integer.MAX_VALUE;
			for(int row = 0; row < height; row++)
		  	{
				for(int column = 0; column < width; column++)
				{
					int elevation = grid[row][column];
					if(elevation < minElevation)
					{
						minElevation = elevation;
					}
					if(elevation > maxElevation)
					{
						maxElevation = elevation;
					}
				}
		  	}
			double interval = (maxElevation - minElevation) / 255.0;
			
			for (int i = 0; i < height; i++)
			{
				for (int j = 0; j < width; j++) {
					int value = (int)((grid[i][j] - minElevation) / interval);
					writer.write(String.valueOf(value));
					if(j != width - 1)
					{
						writer.write(" ");
					}
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Get the most cost-efficient path from the source Point start to the destination Point end
	 * using Dijkstra's algorithm on pixels.
	 * @return the List of Points on the most cost-efficient path from start to end
	 */
	public List<Point> getMostEfficientPath(Point start, Point end) {

		List<Point> path = new ArrayList<>();

		double[][] costTo = new double[height][width];
		Point[][] pointTo = new Point[height][width];
		PriorityQueue<Point> pq = new PriorityQueue<Point>(height * width, new Comparator<Point>()
		{
			@Override
			public int compare(Point p1, Point p2)
			{
				return Double.compare(costTo[p1.x][p1.y], costTo[p2.x][p2.y]);
			}
		});
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				costTo[i][j] = Double.POSITIVE_INFINITY;
				pointTo[i][j] = null;
			}
		}
		
		int temp = start.x;
		start.x = start.y;
		start.y = temp;
		temp = end.x;
		end.x = end.y;
		end.y = temp;

		costTo[start.x][start.y] = 0;
		pq.add(start);
		while(!pq.isEmpty())
		{
			Point pt = pq.remove();
			if(pt == end)
			{
				break;
			}
			ArrayList<Point> neighbourList = new ArrayList<Point>();
			if(pt.x == 0)
			{
				if(pt.y == 0)
				{
					Point pts = new Point(pt.x + 1, pt.y);
					Point pte = new Point(pt.x, pt.y + 1);
					Point ptse = new Point(pt.x + 1, pt.y + 1);
					neighbourList.add(pts);
					neighbourList.add(pte);
					neighbourList.add(ptse);
				}
				else if(pt.y == width - 1)
				{
					Point ptw = new Point(pt.x, pt.y - 1);
					Point pts = new Point(pt.x + 1, pt.y);
					Point ptsw = new Point(pt.x + 1, pt.y - 1);
					neighbourList.add(pts);
					neighbourList.add(ptw);
					neighbourList.add(ptsw);
				}
				else
				{
					Point ptw = new Point(pt.x, pt.y - 1);
					Point pte = new Point(pt.x, pt.y + 1);
					Point ptse = new Point(pt.x + 1, pt.y + 1);
					Point ptsw = new Point(pt.x + 1, pt.y - 1);
					Point pts = new Point(pt.x + 1, pt.y);
					neighbourList.add(pts);
					neighbourList.add(ptw);
					neighbourList.add(pte);
					neighbourList.add(ptse);
					neighbourList.add(ptsw);
				}
			}
			else if(pt.x == height - 1)
			{
				if(pt.y == 0)
				{
					Point pte = new Point(pt.x, pt.y + 1);
					Point ptn = new Point(pt.x - 1, pt.y);
					Point ptne = new Point(pt.x - 1, pt.y + 1);
					neighbourList.add(ptn);
					neighbourList.add(pte);
					neighbourList.add(ptne);
				}
				else if(pt.y == width - 1)
				{
					Point ptn = new Point(pt.x - 1, pt.y);
					Point ptw = new Point(pt.x, pt.y - 1);
					Point ptnw = new Point(pt.x - 1, pt.y - 1);
					neighbourList.add(ptw);
					neighbourList.add(ptn);
					neighbourList.add(ptnw);
				}
				else
				{
					Point ptn = new Point(pt.x - 1, pt.y);
					Point ptw = new Point(pt.x, pt.y - 1);
					Point pte = new Point(pt.x, pt.y + 1);
					Point ptne = new Point(pt.x - 1, pt.y + 1);
					Point ptnw = new Point(pt.x - 1, pt.y - 1);
					neighbourList.add(ptw);
					neighbourList.add(ptn);
					neighbourList.add(pte);
					neighbourList.add(ptnw);
					neighbourList.add(ptne);
				}
			}
			else if(pt.y == 0)
			{
				Point ptn = new Point(pt.x - 1, pt.y);
				Point ptne = new Point(pt.x - 1, pt.y + 1);
				Point pte = new Point(pt.x, pt.y + 1);
				Point ptse = new Point(pt.x + 1, pt.y + 1);
				Point pts = new Point(pt.x + 1, pt.y);
				neighbourList.add(pts);
				neighbourList.add(ptn);
				neighbourList.add(pte);
				neighbourList.add(ptse);
				neighbourList.add(ptne);
			}
			else if(pt.y == width - 1)
			{
				Point ptn = new Point(pt.x - 1, pt.y);
				Point ptnw = new Point(pt.x - 1, pt.y - 1);
				Point ptw = new Point(pt.x, pt.y - 1);
				Point ptsw = new Point(pt.x + 1, pt.y - 1);
				Point pts = new Point(pt.x + 1, pt.y);
				neighbourList.add(pts);
				neighbourList.add(ptw);
				neighbourList.add(ptn);
				neighbourList.add(ptnw);
				neighbourList.add(ptsw);
			}
			else
			{
				Point ptn = new Point(pt.x - 1, pt.y);
				Point ptnw = new Point(pt.x - 1, pt.y - 1);
				Point ptw = new Point(pt.x, pt.y - 1);
				Point ptsw = new Point(pt.x + 1, pt.y - 1);
				Point pts = new Point(pt.x + 1, pt.y);
				Point ptse = new Point(pt.x + 1, pt.y + 1);
				Point pte = new Point(pt.x, pt.y + 1);
				Point ptne = new Point(pt.x - 1, pt.y + 1);
				neighbourList.add(pts);
				neighbourList.add(ptw);
				neighbourList.add(ptn);
				neighbourList.add(pte);
				neighbourList.add(ptnw);
				neighbourList.add(ptse);
				neighbourList.add(ptne);
				neighbourList.add(ptsw);
			}
			neighbourList.sort(new Comparator<Point>()
			{
				@Override
				public int compare(Point p1, Point p2)
				{
					return Double.compare(grid[p1.x][p1.y], grid[p2.x][p2.y]);
				}
			});
			for(Point pp : neighbourList)
			{
				if(grid[pp.x][pp.y] < maxFlyingHeight)
				{
					double euclid;
					if(pp.x != pt.x && pp.y != pt.y)
					{
						euclid = Math.sqrt(2);
					}
					else
					{
						euclid = 1;
					}
					int heightImpact;
					if(grid[pp.x][pp.y] <= grid[pt.x][pt.y])
					{
						heightImpact = 0;
					}
					else
					{
						heightImpact = grid[pp.x][pp.y] - grid[pt.x][pt.y];
					}
					double cost = euclid * fuelCostPerUnit + heightImpact * climbingCostPerUnit;
					if(costTo[pt.x][pt.y] + cost < costTo[pp.x][pp.y])
					{
						costTo[pp.x][pp.y] = costTo[pt.x][pt.y] + cost;
						pointTo[pp.x][pp.y] = pt;
						if(!pq.contains(pp))
						{
							pq.add(pp);
						}
					}
				}
			}
		}

		Stack<Point> pathStack = new Stack<Point>();
		for(Point ptStack = end; ptStack != null && ptStack != start; ptStack = pointTo[ptStack.x][ptStack.y])
		{
			pathStack.push(ptStack);
		}
		if(pathStack.size() > 1)
		{
			path.add(new Point(start.y, start.x));
			while(!pathStack.isEmpty())
			{
				Point pTemp = pathStack.pop();
				path.add(new Point(pTemp.y, pTemp.x));
			}
		}
		return path;
	}

	/**
	 * Calculate the most cost-efficient path from source to destination.
	 * @return the total cost of this most cost-efficient path when traveling from source to destination
	 */
	public double getMostEfficientPathCost(List<Point> path){
		double totalCost = 0.0;

		for(int i = 1; i < path.size(); i++)
		{
			Point pt = path.get(i-1);
			Point pp = path.get(i);
			double euclid;
			if(pp.x != pt.x && pp.y != pt.y)
			{
				euclid = Math.sqrt(2);
			}
			else
			{
				euclid = 1;
			}
			int heightImpact;
			if(grid[pp.y][pp.x] <= grid[pt.y][pt.x])
			{
				heightImpact = 0;
			}
			else
			{
				heightImpact = grid[pp.y][pp.x] - grid[pt.y][pt.x];
			}
			double cost = euclid * fuelCostPerUnit + heightImpact * climbingCostPerUnit;
			totalCost += cost;
		}

		return totalCost;
	}


	/**
	 * Draw the most cost-efficient path on top of the grayscale map from source to destination.
	 */
	public void drawMostEfficientPath(Graphics g, List<Point> path){
		
		for(Point p : path) 
		{
			g.setColor(new Color(0, 255, 0));
			g.fillRect(p.x, p.y, 1, 1);
		}
	}

	/**
	 * Find an escape path from source towards East such that it has the lowest elevation change.
	 * Choose a forward step out of 3 possible forward locations, using greedy method described in the assignment instructions.
	 * @return the list of Points on the path
	 */
	public List<Point> getLowestElevationEscapePath(Point start){
		List<Point> pathPointsList = new ArrayList<>();

		int temp = start.x;
		start.x = start.y;
		start.y = temp;
		int distanceToEast = width - start.y - 1;

		pathPointsList.add(new Point(start.y, start.x));

		for(int i = 0; i < distanceToEast; i++)
		{
			int baseElevation = grid[start.x][start.y];
			
			int elevationNE = Math.abs(grid[start.x - 1][start.y + 1] - baseElevation);
			int elevationE = Math.abs(grid[start.x][start.y + 1] - baseElevation);
			int elevationSE = Math.abs(grid[start.x + 1][start.y + 1] - baseElevation);

			if(elevationE <= elevationNE && elevationE <= elevationSE)
			{
				start.y += 1;
			}
			else if(elevationNE <= elevationSE)
			{
				start.x -= 1;
				start.y += 1;
			}
			else
			{
				start.x += 1;
				start.y += 1;
			}
			pathPointsList.add(new Point(start.y, start.x));
		}

		return pathPointsList;
	}


	/**
	 * Calculate the escape path from source towards East such that it has the lowest elevation change.
	 * @return the total change in elevation for the entire path
	 */
	public int getLowestElevationEscapePathCost(List<Point> pathPointsList){
		int totalChange = 0;

		for(int i = 1; i < pathPointsList.size(); i++)
		{
			Point pt = pathPointsList.get(i - 1);
			Point pp = pathPointsList.get(i);
			int change = Math.abs(grid[pp.y][pp.x] - grid[pt.y][pt.x]);
			totalChange += change;
		}

		return totalChange;
	}


	/**
	 * Draw the escape path from source towards East on top of the grayscale map such that it has the lowest elevation change.
	 */
	public void drawLowestElevationEscapePath(Graphics g, List<Point> pathPointsList){
		for(Point p : pathPointsList) 
		{
			g.setColor(new Color(255, 255, 0));
			g.fillRect(p.x, p.y, 1, 1);
		}
	}


}

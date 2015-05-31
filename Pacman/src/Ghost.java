
/**
 * Write a description of class Ghost here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Ghost
{
    //my variables

    private Mode            GhostMode;
    private Mode            PreviousMode;
    private Orientation     GhostOrientation;
    private Orientation     PreviousOrientation;
    //private int[]           GhostScatterTarget;
    private Target          GhostTarget;
    private boolean         dead;
    
    private int             GhostSpeed;

    private java.awt.Color  GhostColour;
    private Point           GhostPosition;
    private Point           OriginalPosition; //to go back home when dead
    
    double a1;
    
    //given
    public static enum Mode{CHASE,SCATTER,PANIC};
    public static enum Orientation{UP,DOWN,LEFT,RIGHT};
    public static enum Target {PACMAN,OFFSET,AIMLESS,SCATTER};
    public static final int[] SPEED={0,1,2,4,8,16,32};
    public static final int OFFSET = 4;
    
    public static final java.awt.Color PANIC_COLOUR = new java.awt.Color(60,100,175);
    
    /*
     * The SPEED indicates the legal speed, representing how many pixels the center of the Pac-Man or the ghost moves per frame. 
     * Note that these speed are proper divisors of the MazeViewer's CELL_SIZE.
     */

    public Ghost(java.awt.Point pos, java.awt.Color colour, int[] scatterTarget){
        /* creates a ghost with a specified position
         * colour
         * a two element integer array specifying the tile position of the scatter target
         * 
         * and sets its initial orientation to Orientation.UP
         * mode to Mode.SCATTER
         * initial travelling speed to CELL_SIZE. 
         */
        
        GhostPosition = pos;
        GhostColour = colour;
        GhostTarget = Target.SCATTER;
        //GhostScatterTarget = scatterTarget;
        GhostOrientation = Orientation.UP;
        PreviousOrientation = GhostOrientation;
        setSpeed(MazeViewer.CELL_SIZE);
        GhostMode = Mode.SCATTER;
        
        dead = false;
        OriginalPosition = GhostPosition;
        PreviousMode = GhostMode;
    }
    
    public Ghost(java.awt.Point pos, java.awt.Color colour, Orientation ori, int speed, Mode m, Target t, int[] scatterTarget) {
       
        /* 
         * creates a ghost with client specified position, colour, orientation, speed, mode, 
         * target scheme and a two element integer array specifying the tile position of the scatter target. 
         * 
         * Your code should ensure the consistency between the specified mode and target scheme. 
         * Note, only Target.SCATTER can be used in Mode.SCATTER, only Target.AIMLESS can be used in Mode.PANIC.
         */
        
        GhostPosition = pos;
        GhostColour = colour;
        GhostOrientation = ori;
        setSpeed(speed);
        setMode(m, t);
        //GhostScatterTarget = scatterTarget;
        dead = false;
        OriginalPosition = GhostPosition;
        PreviousOrientation = GhostOrientation;
        PreviousMode = GhostMode;
    }
    
    public Mode getMode(){
        //returns the current mode of the ghost
        
        return GhostMode;
        
    }

    public int getSpeed() {
        //returns the travelling speed of the ghost.
        return GhostSpeed;
    }
    
    public void setSpeed(int newSpeed){
        //changes the travelling speed to newSpeed.
        
        //if in speed array
        boolean found = false;
        for(int i = 0; i < SPEED.length; i++){
            if (SPEED[i] == newSpeed) {
                found = true;
                break;
            }
        }
        
        if (found) {
            GhostSpeed = newSpeed;
        } else {
            throw new IllegalArgumentException("Error - Newspeed not in speed array");
        }
            
    }
    
    public java.awt.Color getColour(){
        //returns the colour of the ghost. 
        return GhostColour;
    }
    
    public void setColour(java.awt.Color colour){
        //sets the colour of the ghost. 
        GhostColour = colour;
    }
    
    public void setMode(Mode m, Target t){
        //changes the mode to m and target scheme to t. Again your code should ensure consistencies between the input.
        
       if (
            (t == Target.SCATTER && m == Mode.SCATTER) ||
            (t == Target.AIMLESS && m == Mode.PANIC)||
            ((t == Target.PACMAN || t == Target.OFFSET) && m == Mode.CHASE)
        ){
            GhostMode = m;  
            GhostTarget = t;
        } else {
            throw new IllegalArgumentException("Inconsitency between mode & target scheme");
        }        
    }
    
    public Orientation getOrientation(){
        //returns the current orientation of the ghost.
        return GhostOrientation;
    }
    
    public Point getPosition(){
        //returns the current position of the ghost.
        return GhostPosition;
    }
    
    public boolean isDead(){
        return dead;
    }
    
    public boolean isPanic(){
        //returns true if the ghost is frightened and in PANIC mode.
        return GhostMode == Mode.PANIC;
    }
    
    public static void setPanic(Ghost[] ghosts, boolean panic){
        //sets a set of ghosts in panic mode or release them from panic mode. Remember setting the ghost in PANIC means the target scheme will be AIMLESS. 
        for (int i = 0; i < ghosts.length; i++){
            if(panic) {
                if(ghosts[i].GhostMode != Mode.PANIC)
                    ghosts[i].PreviousMode = ghosts[i].GhostMode;
                ghosts[i].GhostMode = Mode.PANIC;
                ghosts[i].setSpeed(MazeViewer.CELL_SIZE/2);
            } else {
                //return to previous !!!
                ghosts[i].GhostMode = ghosts[i].PreviousMode;
                ghosts[i].setSpeed(MazeViewer.CELL_SIZE);
                
                ghosts[i].GhostPosition.x = ((int) (ghosts[i].GhostPosition.x/2))*2; //round to nearest 2 pixels
                ghosts[i].GhostPosition.y = ((int) (ghosts[i].GhostPosition.y/2))*2; //round to nearest 2 pixels
                
            }
        }
    }
    
    public boolean atGrid(){
        boolean canx = false;
        if ((GhostPosition.x-MazeViewer.CELL_SIZE/2)%MazeViewer.CELL_SIZE == 0) canx = true;
        boolean cany = false;
        if ((GhostPosition.y-MazeViewer.CELL_SIZE/2)%MazeViewer.CELL_SIZE == 0) cany = true;

        return canx && cany;
    }
    
    public void move(Maze maze){
        //automatically moves the ghost in the user specified maze to the next position with proper orientation.
        boolean doMove = false;
        
        PacMan pacman = maze.getPacMan();
        
        //conditions to collide
        
        if((GhostPosition.x == pacman.getPosition().x && GhostPosition.y == pacman.getPosition().y || 
        
        Math.abs(GhostPosition.x - pacman.getPosition().x)== 1 && GhostPosition.y == pacman.getPosition().y ||
        GhostPosition.x == pacman.getPosition().x && Math.abs(GhostPosition.y - pacman.getPosition().y) == 1 || 
        
        Math.abs(GhostPosition.x - pacman.getPosition().x)== 2 && GhostPosition.y == pacman.getPosition().y ||
        GhostPosition.x == pacman.getPosition().x && Math.abs(GhostPosition.y - pacman.getPosition().y) == 2 ) && !dead){
            
            maze.doCollide(isPanic(), GhostPosition);
            if(isPanic()){
                dead = true;
            }
        }
        
        if(atGrid()){
            
            Orientation[] Ori = getOrientations(maze);
            
           if(dead){
                GhostOrientation = targetDirection(OriginalPosition,maze);
                
                if(Math.abs(GhostPosition.x - OriginalPosition.x) <= 4 && Math.abs(GhostPosition.y - OriginalPosition.y) <= 4){
                    dead = false;
                }
            } else {
                switch(GhostMode){
                    case PANIC:
                    //random from possible directions 
                    GhostOrientation = Ori[(int) (Math.random()*Ori.length)];
                    break;
                    
                    case SCATTER: 
                    //follow scatter target
                    //Point target = new Point (GhostScatterTarget[0], GhostScatterTarget[1]);
                    //GhostOrientation = targetDirection(target, maze, true);
                    	GhostOrientation = targetDirection(targetPoint(maze), maze);
                    break;
                    
                    case CHASE:
                    if(GhostTarget == Target.PACMAN){
                        //follow pacman
                        //GhostOrientation = targetDirection(maze.getPacMan().getPosition(), maze);
                    	GhostOrientation = targetDirection(targetPoint(maze), maze);
                    } else {
                        //offset
                        GhostOrientation = targetDirection(targetPoint(maze), maze);
                    }
                }
            }

            switch (maze.locationStatus(nextPos(GhostPosition,GhostOrientation))){
            case INVALID:
            //wrap around to other side of screen
                if (GhostPosition.x == MazeViewer.CELL_SIZE/2){ //left side
                    GhostPosition.x = maze.getMap().length*MazeViewer.CELL_SIZE-MazeViewer.CELL_SIZE/2;
                    
                } else if (GhostPosition.x == maze.getMap().length*MazeViewer.CELL_SIZE-MazeViewer.CELL_SIZE/2){ //right side
                    GhostPosition.x = MazeViewer.CELL_SIZE/2;
                }
            case DEAD:
                //won't happen
                break;
            case ENERGISER:
            case LEGAL:
            case DOT:
            doMove = true;
            }
        } else {
            doMove = true;
        }
        
        if(doMove){
             PreviousOrientation = GhostOrientation;
             GhostPosition = nextPos2(GhostPosition, GhostOrientation);
        }
    }

    private Orientation targetDirection(Point target, Maze maze){
        return targetDirection(target, maze, false);
    }
    
    private Orientation targetDirection(Point target, Maze maze, boolean doScale){
        
        Orientation[] possibleOrientations = getOrientations(maze);

        if(possibleOrientations.length == 1){
            return possibleOrientations[0];
        }
        
        Orientation thisway = possibleOrientations[0];        
        int dist;
        int tempDist;
        
        if(doScale){
            dist = Math.abs(target.x*MazeViewer.CELL_SIZE - nextPos(GhostPosition,possibleOrientations[0]).x) + Math.abs(target.y*MazeViewer.CELL_SIZE - nextPos(GhostPosition,possibleOrientations[0]).y);
        } else {
            dist = Math.abs(target.x - nextPos(GhostPosition,possibleOrientations[0]).x) + Math.abs(target.y - nextPos(GhostPosition,possibleOrientations[0]).y);
        }

        //find shortest route
        for(int i = 0; i < possibleOrientations.length; i++){
            
            if(doScale){
                tempDist = Math.abs(target.x*MazeViewer.CELL_SIZE - nextPos(GhostPosition,possibleOrientations[i]).x) + Math.abs(target.y*MazeViewer.CELL_SIZE - nextPos(GhostPosition,possibleOrientations[i]).y);
            } else {
                tempDist = Math.abs(target.x - nextPos(GhostPosition,possibleOrientations[i]).x) + Math.abs(target.y - nextPos(GhostPosition,possibleOrientations[i]).y);
            }
            
            if(dist > tempDist){
                dist = tempDist;
                thisway = possibleOrientations[i];
            }
            /*
            if(tempDist <= MazeViewer.CELL_SIZE){
                if(Math.random()<0.9){
                    return possibleOrientations[i];
                }
            }
            */
        }

        return thisway;
    }
    
    private Point nextPos(Point positioncopy, Orientation ori){
        Point position = new Point(positioncopy); //translate copy of point
        int dist = MazeViewer.CELL_SIZE;
        switch (ori){
            case UP: 
                position.translate(0,-dist); break;
            case DOWN: 
                position.translate(0,dist); break;
            case LEFT: 
                position.translate(-dist,0); break;
            case RIGHT: 
                position.translate(dist,0); break;
        }
        return position;
    }
    // utility function for evaluating intersections
    private float utility(Maze maze, Point point, Ghost gh){
    	Point ghostpoint = gh.GhostPosition;
    	Maze.Status[][] grid = maze.getMap();
    	int width = grid.length;
    	int height = grid[0].length;
    	ArrayList<Point> energisers = new ArrayList<Point>(); 
    	// finds all the energisers in the maze
    	for(int i = 0; i< width;i++){
    		for(int j = 0; j< height;j++){
        		if(grid[i][j] == Maze.Status.ENERGISER){
        			energisers.add(new Point(i*MazeViewer.CELL_SIZE+MazeViewer.CELL_SIZE/2, j*MazeViewer.CELL_SIZE+MazeViewer.CELL_SIZE/2));
        		}
        	}
    	}
    	// finds the closest energiser to pacman
    	if(energisers.size() > 0){
    		Point closest = energisers.get(0);
    		int minedist = 2000;
    		for(Point e: energisers){
    			if(minedist > distance(maze.getPacMan().getPosition(), e)){
    				minedist = distance(maze.getPacMan().getPosition(), e);
    				closest = e;
    			}
    		}
    		int mindist = 2000;
    		//finds the minimum distance of ghosts from the closest energiser
    		for(Ghost g: maze.getGhosts()){
    			Math.min(mindist, ghostDistance(maze, g, closest));
    		}
    		// if minimum distance of ghosts is greater than pacman and inside of safe distance, utility = 0 
    		if((minedist < mindist) && (distance(point,closest) < a1))
    			return 0.0f;
    		else
    		{
    			float d = 1000 - (ghostDistance(maze, gh, point) - distance(maze.getPacMan().getPosition(), point));
    			if(d > 1000)
    				return 0.0f;
    			else
    				return d;
    		}
    	}
    	else
    	{
    		// set utility to difference between distance from point to pacman and point to ghost
    		float d = 1000 - (ghostDistance(maze, gh, point) - distance(maze.getPacMan().getPosition(), point));
			if(d > 1000)
				return 0.0f;
			else
				return d;
    	}
    }
    // Returns target point for ghost, used as target for move function
    private Point targetPoint(Maze maze){
		Queue<Point> intqueue = new LinkedList<Point>();
		intqueue.addAll(getIntersections(maze, maze.getPacMan().getPosition()));
		ArrayList<Point> visited = new ArrayList<Point>();
		//creates a utility array and sets values to 0.0f
		Float[][] utilities = new Float[20000][4];
		for(int i = 0;i<20000;i++){
			for(int j = 0;j<4;j++){
				utilities[i][j] = 0.0f;
			}
		}
		ArrayList<Point> targets = new ArrayList<Point>();
		//starts off with initial intersections and expands those with utility of 0
		targets.addAll(intqueue);
		int i = 0;
		while(!intqueue.isEmpty()){
			Point currentpoint = intqueue.remove();
			visited.add(currentpoint);
			boolean expand = false;
			// calculates utility of intersection puts it in utility array
			for(int g = 0; g<4 ;g++){
				float u = utility(maze, currentpoint, maze.getGhosts()[g]);
				utilities[i][g] = u;
				if(u == 0){expand = true;}
			}
			//if utility is 0 then expands the intersection, adding it onto the queue
			if(expand || (intqueue.isEmpty() && i<4)){
				targets.addAll(getIntersections(maze, currentpoint));
				intqueue.addAll(targets);
				intqueue.removeAll(visited);
			}
			i++;
		}
		//selects intersection targets for each ghost
		Point[] ghosttargets = new Point[4];
		for(int g = 0; g<4; g++){
			ghosttargets[g] = maze.getPacMan().getPosition();
		}
		//maximises utility based on utilities table, so best fit of ghost to bounding intersection
		for(int g = 0;g<4;g++){
			float best = 0;
			int chosen = 0;
			for(int j = 0;j<targets.size();j++){
				if(best < utilities[j][g]){
					best = utilities[j][g];
					ghosttargets[g] = targets.get(j);
					chosen = j;
				}
			}
			for(int p = g; p<4;p++){
				utilities[chosen][p] = 0.0f;
			}
		}
		//System.out.println(ghosttargets[0].x/MazeViewer.CELL_SIZE + ", " + ghosttargets[0].y/MazeViewer.CELL_SIZE);
		//System.out.println(ghosttargets[1].x/MazeViewer.CELL_SIZE + ", " + ghosttargets[1].y/MazeViewer.CELL_SIZE);
		//System.out.println(ghosttargets[2].x/MazeViewer.CELL_SIZE + ", " + ghosttargets[2].y/MazeViewer.CELL_SIZE);
		//System.out.println(ghosttargets[3].x/MazeViewer.CELL_SIZE + ", " + ghosttargets[3].y/MazeViewer.CELL_SIZE + "/n");
		//System.out.println(maze.getMap()[1][6] == Maze.Status.ENERGISER);
		for(int g = 0;g<4;g++){
			if(maze.getGhosts()[g].equals(this)){
				return ghosttargets[g];
			}
		}
		return null;
    }
    
    private int distance(Point point1, Point point2){
    	return Math.abs(point1.x-point2.x) + Math.abs(point1.y-point2.y);
    }
    // calculates distance given the ghosts current inertia (distance from ghost to next intersection + ghost to point)
    private int ghostDistance(Maze maze, Ghost g, Point point){
		ArrayList<Point> intersections = getIntersections(maze, point);
    	Point chosen = intersections.get(0);
    	switch(g.GhostOrientation){
    		case UP:
    			int best = 0;
    			for(Point p: intersections){
    				if(best < p.y-g.GhostPosition.y){
    					best = p.y-g.GhostPosition.y;
    					chosen = p;
    				}
    			}
    		case LEFT:
    			int best2 = 0;
    			for(Point p: intersections){
    				if(best2 < p.x-g.GhostPosition.x){
    					best2 = p.x-g.GhostPosition.x;
    					chosen = p;
    				}
    			}
    		case DOWN:
    			int best3 = 0;
    			for(Point p: intersections){
    				if(best3 < g.GhostPosition.y - p.y){
    					best3 = g.GhostPosition.y - p.y;
    					chosen = p;
    				}
    			}
    		default:
    			int best4 = 0;
    			for(Point p: intersections){
    				if(best4 < g.GhostPosition.x-p.x){
    					best4 = g.GhostPosition.x-p.x;
    					chosen = p;
    				}
    			}
    	}
		return distance(g.GhostPosition, chosen) + distance(chosen, point);
    }
    // Returns the 2 bounding intersections for a non intersection and the 3 or 4 connected intersections for an intersection
    private ArrayList<Point> getIntersections(Maze maze, Point point){
    	//for(int g = 0;g<maze.getMap()[0].length;g++){
    		//for(int a = 0;a<maze.getMap().length;a++){
        	//	System.out.print(maze.getMap()[a][g] + "  ");
        	//}
    		//System.out.println("");
    	//}
    	//starts with the point's position and adds it to the queue
    	int[] start = {point.x/MazeViewer.CELL_SIZE, point.y/MazeViewer.CELL_SIZE};
    	Queue<int[]> pointqueue = new LinkedList<int[]>();
    	pointqueue.add(start);
    	Maze.Status[][] statuses = maze.getMap();
    	// keeps a array for visited points
    	int[][] visited = new int[statuses.length][statuses[0].length];
		ArrayList<Point> intersections = new ArrayList<Point>();
		//dequeues and checks if it is an intersection, otherwise continues until reaching an intersection
		while(!pointqueue.isEmpty() && intersections.size() < 5){
			int[] current = pointqueue.remove();
			int x = current[0];
			int y = current[1];
			visited[x][y] = 1;
			int path = 0;
			//checks left right up down of current point to check if it is an intersection
			if((x-1)>=0){
				if(statuses[x-1][y] != Maze.Status.DEAD && visited[x-1][y] != 1){
					int[] newpoint = {x-1,y};
					pointqueue.add(newpoint);
					path++;
				}
			}
			if((y+1)<=statuses[0].length-1){
				if(statuses[x][y+1] != Maze.Status.DEAD && visited[x][y+1] != 1){
					int[] newpoint = {x,y+1};
					pointqueue.add(newpoint);
					path++;
				}
			}
			if((y-1)>=0){
				if(statuses[x][y-1] != Maze.Status.DEAD && visited[x][y-1] != 1){
					int[] newpoint = {x,y-1};
					pointqueue.add(newpoint);
					path++;
				}
			}
			if((x+1) <= statuses.length-1){
				if(statuses[x+1][y] != Maze.Status.DEAD && visited[x+1][y] != 1){
					int[] newpoint = {x+1,y};
					pointqueue.add(newpoint);
					path++;
				}
			}
			// if there are more than 2 paths then it is an intersection
			if(path > 2){
				intersections.add(new Point(x*MazeViewer.CELL_SIZE + MazeViewer.CELL_SIZE/2,y*MazeViewer.CELL_SIZE+MazeViewer.CELL_SIZE/2));
			}
		}
    	return intersections;
    }
    
    private Point nextPos2(Point positioncopy, Orientation ori){
        Point position = new Point(positioncopy); //translate copy of point
        int dist = GhostSpeed/(MazeViewer.CELL_SIZE/2); //ghost speed
        switch (ori){
            case UP: 
                position.translate(0,-dist); break;
            case DOWN: 
                position.translate(0,dist); break;
            case LEFT: 
                position.translate(-dist,0); break;
            case RIGHT: 
                position.translate(dist,0); break;
        }
        return position;
    }
    
    /*private Point offset(Point positioncopy, Orientation ori){
        Point position = new Point(positioncopy); //translate copy of point
        int dist = MazeViewer.CELL_SIZE*4; //for offset
        switch (ori){
            case UP: 
                position.translate(0,-dist); break;
            case DOWN: 
                position.translate(0,dist); break;
            case LEFT: 
                position.translate(-dist,0); break;
            case RIGHT: 
                position.translate(dist,0); break;
        }
        return position;
    }*/
    
    private Orientation[] getOrientations(Maze maze){
        //up down left right
        
        boolean[] temp = new boolean[4];
        
        for(int i = 0; i < 4; i++){
            temp[i] = false; //set all directions not possible
        }
        
        if(!(maze.locationStatus(nextPos(GhostPosition, Orientation.UP)) == Maze.Status.DEAD)){ //if maze is not dead    
            if(GhostOrientation != Orientation.DOWN){ //and haven't just come that way
                temp[0] = true; //direction is possible
            }
        } 
        
        if(!(maze.locationStatus(nextPos(GhostPosition, Orientation.DOWN)) == Maze.Status.DEAD)){ 
            if(GhostOrientation != Orientation.UP){
                temp[1] = true;
            }
        } 
        
        if(!(maze.locationStatus(nextPos(GhostPosition, Orientation.LEFT)) == Maze.Status.DEAD)){
            if(GhostOrientation != Orientation.RIGHT && PreviousOrientation != Orientation.RIGHT){
                temp[2] = true;
            }
        } 
        
        if(!(maze.locationStatus(nextPos(GhostPosition, Orientation.RIGHT)) == Maze.Status.DEAD)){
            if(GhostOrientation != Orientation.LEFT && PreviousOrientation != Orientation.LEFT){
                temp[3] = true;
            }
        }
        
        int count = 0;
        
        for(int i = 0; i < 4; i++){
            if(temp[i]){
                count++; //count possible directions
            }
        }
        
        Orientation[] tempOri = new Orientation[count]; //new array of possible directions
        
        int i = 0;
        
        if(temp[0]){
            tempOri[i]=Orientation.UP; //populate array
            i++;
        }
        
        if(temp[1]){
            tempOri[i]=Orientation.DOWN;
            i++;
        }
        
        if(temp[2]){
            tempOri[i]=Orientation.LEFT;
            i++;
        }
        
        if(temp[3]){
            tempOri[i]=Orientation.RIGHT;
        }
        
        return tempOri;
    }
    
    static public void setToOriginal(Ghost[] ghosts){
        
        for(int i = 0; i < ghosts.length; i++){
            
            ghosts[i].GhostPosition = ghosts[i].OriginalPosition;
            
        }
    }
}

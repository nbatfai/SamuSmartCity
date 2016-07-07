/**
 * @brief Samu Car Window (monitor)
 *
 * @file CarWindow.java
 * @author Norbert Bátfai <nbatfai@gmail.com>
 * @version 0.0.16
 *
 * @section LICENSE
 *
 * Copyright (C) 2014, 2015, 2016 Norbert Bátfai, batfai.norbert@inf.unideb.hu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * @section DESCRIPTION
 *
 * Justine - this is a rapid prototype for development of Robocar City Emulator
 * Justine Car Window (a monitor program for Robocar City Emulator)
 *
 * SamuSmartCity - Everybody has been travelling in a car since nobody got a car.
 * This project is an example program of the paper entitled "Practical
 * Robopsychology: Samu Has Learned the Traffic of the City".
 *
 */
package justine.robocar;


class SamuOverlay implements org.jxmapviewer.painter.Painter<org.jxmapviewer.JXMapViewer>
{
    double minlat=47.4793, minlon= 21.5579, maxlat=47.5735, maxlon=21.6915;
    int scale = 100;
    int cells[][];
    double dx, dy;

    public SamuOverlay ( int scale )
    {
        this.scale = scale;

        dx= ( maxlat - minlat ) / ( ( double ) scale );
        dy= ( maxlon - minlon ) / ( ( double ) scale );

        cells = new int[scale][scale];
    }

    public void clearCells()
    {

        for ( int i=0; i<scale ; ++i )
            for ( int j=0; j<scale ; ++j ) {
                cells[i][j] = 0;
            }

    }

    public void setCell ( int i, int j )
    {
        ++cells[i][j];
    }

    public void setCell ( double lat, double lon )
    {

        int i= ( int ) ( ( lat-minlat ) / dx );
        int j= ( int ) ( ( lon-minlon ) / dy );

        if ( 0<=i && i<scale && 0<=j && j<scale ) {
            ++cells[i][j];
        }
    }

    int numberOfNeighbors ( int r, int c )
    {
        int number = 0;
        for ( int i =-1; i<2; ++i )
            for ( int j =-1; j<2; ++j )

                //if ( ! ( ( i==0 ) && ( j==0 ) ) )
            {
                int o = c + j;
                if ( o < 0 ) {
                    o = scale-1;
                } else if ( o >= scale ) {
                    o = 0;
                }

                int s = r + i;
                if ( s < 0 ) {
                    s = scale-1;
                } else if ( s >= scale ) {
                    s = 0;
                }

                number += cells[s][o];
            }

        return number;
    }

    public void paint ( java.awt.Graphics2D g2d, org.jxmapviewer.JXMapViewer jXMapV, int width, int height )
    {

        g2d = ( java.awt.Graphics2D ) g2d.create();
        g2d.translate ( -jXMapV.getViewportBounds().x, -jXMapV.getViewportBounds().y );

        for ( int i=0; i<scale ; ++i )
            for ( int j=0; j<scale ; ++j ) {

                org.jxmapviewer.viewer.GeoPosition geop = new org.jxmapviewer.viewer.GeoPosition ( minlat+ ( i+1 ) *dx, minlon+j*dy );
                java.awt.geom.Point2D pt = jXMapV.getTileFactory().geoToPixel ( geop, jXMapV.getZoom() );

                int x = ( int ) pt.getX();
                int y = ( int ) pt.getY();

                geop = new org.jxmapviewer.viewer.GeoPosition ( minlat+ ( i+1 ) *dx, minlon+ ( j+1 ) *dy );
                pt = jXMapV.getTileFactory().geoToPixel ( geop, jXMapV.getZoom() );

                int mx = ( int ) pt.getX()-x;

                geop = new org.jxmapviewer.viewer.GeoPosition ( minlat+i*dx, minlon+ ( j+1 ) *dy ) ;

                pt = jXMapV.getTileFactory().geoToPixel ( geop, jXMapV.getZoom() );

                int my = ( int ) pt.getY()-y ;

                java.awt.Rectangle cellRect = new java.awt.Rectangle ( x, y, mx, my );

                // if ( ( i+j ) %2 == 0 ) {
                int n = numberOfNeighbors ( i, j ) ;
                if ( n > 1 ) {
                    g2d.setColor ( new java.awt.Color ( n*15%255, ( n/3 ) *30%255, ( n/2 ) *8%255,120 ) );
                    g2d.fill ( cellRect );
                    g2d.draw ( cellRect );
                } else {
                    g2d.setColor ( java.awt.Color.LIGHT_GRAY );
                    g2d.draw ( cellRect );
                }

            }

        g2d.dispose();
    }
}

class Traffic
{

    public java.util.Set<org.jxmapviewer.viewer.Waypoint> waypoints;
    public String title;

    public Traffic ( java.util.Set<org.jxmapviewer.viewer.Waypoint> waypoints, String title )
    {

        this.waypoints = waypoints;
        this.title = title;

    }

}

class WaypointPolice implements org.jxmapviewer.viewer.Waypoint
{

    org.jxmapviewer.viewer.GeoPosition geoPosition;
    String name;

    public WaypointPolice ( double lat, double lon, String name )
    {
        geoPosition = new org.jxmapviewer.viewer.GeoPosition ( lat, lon );
        this.name = name;
    }

    @Override
    public org.jxmapviewer.viewer.GeoPosition getPosition()
    {
        return geoPosition;
    }

    String getName()
    {

        return name;
    }
}

class WaypointGangster implements org.jxmapviewer.viewer.Waypoint
{

    org.jxmapviewer.viewer.GeoPosition geoPosition;

    public WaypointGangster ( double lat, double lon )
    {
        geoPosition = new org.jxmapviewer.viewer.GeoPosition ( lat, lon );
    }

    @Override
    public org.jxmapviewer.viewer.GeoPosition getPosition()
    {
        return geoPosition;
    }
}

class WaypointCaught implements org.jxmapviewer.viewer.Waypoint
{

    org.jxmapviewer.viewer.GeoPosition geoPosition;

    public WaypointCaught ( double lat, double lon )
    {
        geoPosition = new org.jxmapviewer.viewer.GeoPosition ( lat, lon );
    }

    @Override
    public org.jxmapviewer.viewer.GeoPosition getPosition()
    {
        return geoPosition;
    }
}

class Loc
{

    double lat;
    double lon;

    public Loc ( double lat, double lon )
    {

        this.lat = lat;
        this.lon = lon;

    }

}

public class CarWindow extends javax.swing.JFrame
{

    org.jxmapviewer.viewer.WaypointPainter<org.jxmapviewer.viewer.Waypoint> waypointPainter
        = new org.jxmapviewer.viewer.WaypointPainter<org.jxmapviewer.viewer.Waypoint>();
    org.jxmapviewer.viewer.GeoPosition[] geopos
        = new org.jxmapviewer.viewer.GeoPosition[4];
    org.jxmapviewer.JXMapViewer jXMapViewer
        = new org.jxmapviewer.JXMapViewer();

    SamuOverlay samuOverlay = new SamuOverlay ( 100 );

    java.util.Map<Long, Loc> lmap = null;
    java.io.File tfile = null;
    java.util.Random rnd = new java.util.Random();
    java.util.Scanner scan = null;

    String hostname = "localhost";
    int port = 10007;

    java.awt.Robot robot;

    javax.swing.SwingWorker<Void, Traffic> worker = new javax.swing.SwingWorker<Void, Traffic>()
    {

        @Override
        protected Void doInBackground() throws Exception {

            try {
                java.net.Socket trafficServer = new java.net.Socket ( hostname, port );
                java.io.OutputStream os = trafficServer.getOutputStream();
                java.io.DataOutputStream dos
                    = new java.io.DataOutputStream ( os );

                dos.writeUTF ( "<disp>" );
                java.io.InputStream is = trafficServer.getInputStream();

                scan = new java.util.Scanner ( is );

                for ( ;; ) {
                    java.util.Set<org.jxmapviewer.viewer.Waypoint> waypoints
                        = new java.util.HashSet<org.jxmapviewer.viewer.Waypoint>();

                    int time = 0, size = 0, minutes = 0;

                    time = scan.nextInt();
                    minutes = scan.nextInt();
                    size = scan.nextInt();

                    long ref_from = 0, ref_to = 0;
                    int step = 0, maxstep = 1, type = 0;
                    double lat, lon;
                    double lat2, lon2;
                    int num_captured_gangsters;
                    String name = "Cop";

                    java.util.Map<String, Integer> cops = new java.util.HashMap<String, Integer>();

                    samuOverlay.clearCells();

                    for ( int i = 0; i < size; ++i ) {

                        ref_from = scan.nextLong();
                        ref_to = scan.nextLong();
                        maxstep = scan.nextInt();
                        step = scan.nextInt();
                        type = scan.nextInt();

                        if ( type == 1 ) {
                            num_captured_gangsters = scan.nextInt();
                            name = scan.next();
                            String nname = name.substring ( 0, 7 );

                            if ( cops.containsKey ( nname ) ) {
                                cops.put ( nname, cops.get ( nname ) + num_captured_gangsters );
                            } else {
                                cops.put ( nname, num_captured_gangsters );
                            }
                        }

                        lat = lmap.get ( ref_from ).lat;
                        lon = lmap.get ( ref_from ).lon;

                        lat2 = lmap.get ( ref_to ).lat;
                        lon2 = lmap.get ( ref_to ).lon;

                        if ( maxstep == 0 ) {
                            maxstep = 1;
                        }

                        lat += step * ( ( lat2 - lat ) / maxstep );
                        lon += step * ( ( lon2 - lon ) / maxstep );

                        if ( type == 1 ) {
                            waypoints.add ( new WaypointPolice ( lat, lon, name ) );
                        } else if ( type == 2 ) {
                            waypoints.add ( new WaypointGangster ( lat, lon ) );
                            samuOverlay.setCell ( lat, lon );
                        } else if ( type == 3 ) {
                            waypoints.add ( new WaypointCaught ( lat, lon ) );
                        } else if ( type == 4 ) {
                            //waypoints.add(new WaypointCaught(lat, lon));
                        } else {
                            waypoints.add ( new org.jxmapviewer.viewer.DefaultWaypoint ( lat, lon ) );
                        }

                    }

                    /*
                    if ( time >= minutes * 60 * 1000 / 200 ) {
                        scan = null;
                    }
                    */

                    StringBuilder sb = new StringBuilder();

                    int sec = time / 5;
                    int min = sec / 60;
                    sec = sec - min * 60;
                    time = time - min * 60 * 5 - sec * 5;

                    sb.append ( "|" );
                    sb.append ( min );
                    sb.append ( ":" );
                    sb.append ( sec );
                    sb.append ( ":" );
                    sb.append ( 2 * time );
                    sb.append ( "|" );
                    //sb.append(" Justine - Car Window (log player for Robocar City Emulator, Robocar World Championshin in Debrecen)");
                    sb.append ( java.util.Arrays.toString ( cops.entrySet().toArray() ) );

                    publish ( new Traffic ( waypoints, sb.toString() ) );

                }

            } catch ( java.io.IOException e ) {

                System.out.println ( e.toString() );

                CarWindow.this.dispatchEvent (
                    new java.awt.event.WindowEvent ( CarWindow.this,
                                                     java.awt.event.WindowEvent.WINDOW_CLOSING ) );
            }

            return null;
        }

        @Override
        protected void process ( java.util.List<Traffic> traffics ) {

            Traffic traffic = traffics.get ( traffics.size() - 1 );
            setTitle ( traffic.title );
            waypointPainter.setWaypoints ( traffic.waypoints );

            jXMapViewer.repaint();
            repaint();

        }

        @Override
        protected void done() {
        }
    };

    javax.swing.Action paintTimer = new javax.swing.AbstractAction()
    {

        public void actionPerformed ( java.awt.event.ActionEvent event ) {

            java.util.Set<org.jxmapviewer.viewer.Waypoint> waypoints
                = new java.util.HashSet<org.jxmapviewer.viewer.Waypoint>();

            if ( scan != null ) {

                try {

                    int time = 0, size = 0, minutes = 0;

                    time = scan.nextInt();
                    minutes = scan.nextInt();
                    size = scan.nextInt();

                    long ref_from = 0, ref_to = 0;
                    int step = 0, maxstep = 1, type = 0;
                    double lat, lon;
                    double lat2, lon2;
                    int num_captured_gangsters;
                    String name = "Cop";

                    java.util.Map<String, Integer> cops = new java.util.HashMap<String, Integer>();

                    for ( int i = 0; i < size; ++i ) {

                        ref_from = scan.nextLong();
                        ref_to = scan.nextLong();
                        maxstep = scan.nextInt();
                        step = scan.nextInt();
                        type = scan.nextInt();

                        if ( type == 1 ) {
                            num_captured_gangsters = scan.nextInt();
                            name = scan.next();

                            if ( cops.containsKey ( name ) ) {
                                cops.put ( name, cops.get ( name ) + num_captured_gangsters );
                            } else {
                                cops.put ( name, num_captured_gangsters );
                            }
                        }

                        lat = lmap.get ( ref_from ).lat;
                        lon = lmap.get ( ref_from ).lon;

                        lat2 = lmap.get ( ref_to ).lat;
                        lon2 = lmap.get ( ref_to ).lon;

                        if ( maxstep == 0 ) {
                            maxstep = 1;
                        }

                        lat += step * ( ( lat2 - lat ) / maxstep );
                        lon += step * ( ( lon2 - lon ) / maxstep );

                        if ( type == 1 ) {
                            waypoints.add ( new WaypointPolice ( lat, lon, name ) );
                        } else if ( type == 2 ) {
                            waypoints.add ( new WaypointGangster ( lat, lon ) );
                        } else if ( type == 3 ) {
                            waypoints.add ( new WaypointCaught ( lat, lon ) );
                        } else {
                            waypoints.add ( new org.jxmapviewer.viewer.DefaultWaypoint ( lat, lon ) );
                        }

                    }

                    if ( time >= minutes * 60 * 1000 / 200 ) {
                        scan = null;
                    }

                    StringBuilder sb = new StringBuilder();

                    int sec = time / 5;
                    int min = sec / 60;
                    sec = sec - min * 60;
                    time = time - min * 60 * 5 - sec * 5;

                    sb.append ( "|" );
                    sb.append ( min );
                    sb.append ( ":" );
                    sb.append ( sec );
                    sb.append ( ":" );
                    sb.append ( 2 * time );
                    sb.append ( "|" );
                    //sb.append(" Justine - Car Window (log player for Robocar City Emulator, Robocar World Championshin in Debrecen)");
                    sb.append ( java.util.Arrays.toString ( cops.entrySet().toArray() ) );

                    setTitle ( sb.toString() );
                    waypointPainter.setWaypoints ( waypoints );

                    jXMapViewer.repaint();
                    repaint();

                } catch ( java.util.InputMismatchException imE ) {

                    java.util.logging.Logger.getLogger (
                        CarWindow.class.getName() ).log ( java.util.logging.Level.SEVERE, "Hibás bemenet...", imE );

                } catch ( java.util.NoSuchElementException e ) {

                    java.util.logging.Logger.getLogger (
                        CarWindow.class.getName() ).log ( java.util.logging.Level.SEVERE, "Tervezett leállás: input végét kapott el a kivételkezelő." );

                    CarWindow.this.dispatchEvent (
                        new java.awt.event.WindowEvent ( CarWindow.this,
                                                         java.awt.event.WindowEvent.WINDOW_CLOSING ) );
                }

            }

        }

    };

    public CarWindow ( double lat, double lon, java.util.Map<Long, Loc> lmap, String hostname, int port )
    {

        this.lmap = lmap;
        this.hostname = hostname;
        this.port = port;

        final org.jxmapviewer.viewer.TileFactory tileFactoryArray[] = {
            new org.jxmapviewer.viewer.DefaultTileFactory (
                new org.jxmapviewer.OSMTileFactoryInfo() ),
            new org.jxmapviewer.viewer.DefaultTileFactory (
                new org.jxmapviewer.VirtualEarthTileFactoryInfo ( org.jxmapviewer.VirtualEarthTileFactoryInfo.MAP ) ),
            new org.jxmapviewer.viewer.DefaultTileFactory (
                new org.jxmapviewer.VirtualEarthTileFactoryInfo ( org.jxmapviewer.VirtualEarthTileFactoryInfo.SATELLITE ) ),
            new org.jxmapviewer.viewer.DefaultTileFactory (
                new org.jxmapviewer.VirtualEarthTileFactoryInfo ( org.jxmapviewer.VirtualEarthTileFactoryInfo.HYBRID ) )

        };

        org.jxmapviewer.viewer.GeoPosition debrecen
            = new org.jxmapviewer.viewer.GeoPosition ( lat, lon );

        javax.swing.event.MouseInputListener mouseListener
            = new org.jxmapviewer.input.PanMouseInputListener ( jXMapViewer );
        jXMapViewer.addMouseListener ( mouseListener );
        jXMapViewer.addMouseMotionListener ( mouseListener );
        jXMapViewer.addMouseListener (
            new org.jxmapviewer.input.CenterMapListener ( jXMapViewer ) );
        jXMapViewer.addMouseWheelListener (
            new org.jxmapviewer.input.ZoomMouseWheelListenerCursor ( jXMapViewer ) );

        jXMapViewer.addKeyListener (
            new org.jxmapviewer.input.PanKeyListener ( jXMapViewer ) );

        jXMapViewer.setTileFactory ( tileFactoryArray[0] );

        ClassLoader classLoader = this.getClass().getClassLoader();

        final java.awt.Image markerImg
            = new javax.swing.ImageIcon ( classLoader.getResource ( "logo1.png" ) ).getImage();
        final java.awt.Image markerImgPolice
            = new javax.swing.ImageIcon ( classLoader.getResource ( "logo2.png" ) ).getImage();
        final java.awt.Image markerImgGangster
            = new javax.swing.ImageIcon ( classLoader.getResource ( "logo3.png" ) ).getImage();
        final java.awt.Image markerImgCaught
            = new javax.swing.ImageIcon ( classLoader.getResource ( "logo4.png" ) ).getImage();

        waypointPainter.setRenderer (
        new org.jxmapviewer.viewer.WaypointRenderer<org.jxmapviewer.viewer.Waypoint>() {
            @Override
            public void paintWaypoint ( java.awt.Graphics2D g2d, org.jxmapviewer.JXMapViewer jXMapV,
            org.jxmapviewer.viewer.Waypoint w ) {

                java.awt.geom.Point2D point = jXMapV.getTileFactory().geoToPixel (
                                                  w.getPosition(), jXMapV.getZoom() );

                if ( w instanceof WaypointPolice ) {
                    g2d.drawImage ( markerImgPolice, ( int ) point.getX() - markerImgPolice.getWidth ( jXMapV ),
                                    ( int ) point.getY() - markerImgPolice.getHeight ( jXMapV ), null );

                    g2d.setFont ( new java.awt.Font ( "Serif", java.awt.Font.BOLD, 14 ) );
                    java.awt.FontMetrics fm = g2d.getFontMetrics();
                    int nameWidth = fm.stringWidth ( ( ( WaypointPolice ) w ).getName() );
                    g2d.setColor ( java.awt.Color.GRAY );
                    java.awt.Rectangle rect = new java.awt.Rectangle ( ( int ) point.getX(), ( int ) point.getY(), nameWidth + 4, 20 );
                    g2d.fill ( rect );
                    g2d.setColor ( java.awt.Color.CYAN );
                    g2d.draw ( rect );
                    g2d.setColor ( java.awt.Color.WHITE );
                    g2d.drawString ( ( ( WaypointPolice ) w ).getName(), ( int ) point.getX() + 2, ( int ) point.getY() + 20 - 5 );

                } else if ( w instanceof WaypointGangster ) {
                    g2d.drawImage ( markerImgGangster, ( int ) point.getX() - markerImgGangster.getWidth ( jXMapV ),
                                    ( int ) point.getY() - markerImgGangster.getHeight ( jXMapV ), null );
                } else if ( w instanceof WaypointCaught ) {
                    g2d.drawImage ( markerImgCaught, ( int ) point.getX() - markerImgCaught.getWidth ( jXMapV ),
                                    ( int ) point.getY() - markerImgCaught.getHeight ( jXMapV ), null );
                } else {
                    g2d.drawImage ( markerImg, ( int ) point.getX() - markerImg.getWidth ( jXMapV ),
                                    ( int ) point.getY() - markerImg.getHeight ( jXMapV ), null );
                }
            }
        } );


        org.jxmapviewer.painter.CompoundPainter<org.jxmapviewer.JXMapViewer> painters
            = new org.jxmapviewer.painter.CompoundPainter<org.jxmapviewer.JXMapViewer>();
        painters.addPainter ( waypointPainter );
        painters.addPainter ( samuOverlay );
        painters.setCacheable ( false );
        //jXMapViewer.setDrawTileBorders ( true );
        jXMapViewer.setOverlayPainter ( painters );
        jXMapViewer.setZoom ( 9 );
        jXMapViewer.setAddressLocation ( debrecen );
        jXMapViewer.setCenterPosition ( debrecen );

        jXMapViewer.addKeyListener ( new java.awt.event.KeyAdapter() {
            int index = 0;

            public void keyPressed ( java.awt.event.KeyEvent evt ) {

                if ( evt.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE ) {
                    jXMapViewer.setTileFactory ( tileFactoryArray[++index % 4] );
                    jXMapViewer.repaint();
                    repaint();
                } else if ( evt.getKeyCode() == java.awt.event.KeyEvent.VK_S ) {
                    jXMapViewer.repaint();
                    repaint();

                    shootScreenshot ( robot.createScreenCapture ( new java.awt.Rectangle (
                                          getLocation().x, getLocation().y,
                                          getSize().width, getSize().height ) ) );

                }

            }
        } );


        setTitle ( "Samu Car Window" );
        getContentPane().add ( jXMapViewer );

        setSize ( 800, 600 );
        setLocationRelativeTo ( null );
        setDefaultCloseOperation ( javax.swing.JFrame.EXIT_ON_CLOSE );

        try {
            robot = new java.awt.Robot (
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice() );
        } catch ( java.awt.AWTException e ) {
            java.util.logging.Logger.getLogger (
                CarWindow.class.getName() ).log ( java.util.logging.Level.SEVERE, "Nem lesz pillanatfelvétel...", e );
        }

        worker.execute();

    }

    public void shootScreenshot ( java.awt.image.BufferedImage mapview )
    {

        StringBuffer sb = new StringBuffer();
        sb = sb.delete ( 0, sb.length() );
        sb.append ( "Raxicab-" );
        sb.append ( new java.util.Date() );
        sb.append ( ".png" );

        try {
            javax.imageio.ImageIO.write ( mapview, "png",
                                          new java.io.File ( sb.toString() ) );

        } catch ( java.io.IOException e ) {
            java.util.logging.Logger.getLogger (
                CarWindow.class.getName() ).log ( java.util.logging.Level.SEVERE, "Pillanatfelvétel hiba...", e );
        }
    }

    public static void readMap ( java.util.Map<Long, Loc> lmap, String name )
    {

        java.util.Scanner scan;
        java.io.File file = new java.io.File ( name );

        long ref = 0;
        double lat = 0.0, lon = 0.0;
        try {

            scan = new java.util.Scanner ( file );

            while ( scan.hasNext() ) {

                ref = scan.nextLong();
                lat = scan.nextDouble();
                lon = scan.nextDouble();

                lmap.put ( ref, new Loc ( lat, lon ) );

            }

        } catch ( Exception e ) {

            java.util.logging.Logger.getLogger (
                CarWindow.class
                .getName() ).log ( java.util.logging.Level.SEVERE, "hibás noderef2GPS leképezés", e );

        }

    }

    public static void main ( String[] args )
    {

        final java.util.Map<Long, Loc> lmap = new java.util.HashMap<Long, Loc>();

        if ( args.length == 1 ) {

            readMap ( lmap, args[0] );

            javax.swing.SwingUtilities.invokeLater ( new Runnable() {
                public void run() {

                    java.util.Map.Entry<Long, Loc> e = lmap.entrySet().iterator().next();

                    new CarWindow ( e.getValue().lat, e.getValue().lon, lmap, "localhost", 10007 ).setVisible ( true );
                }
            } );

        } else if ( args.length == 2 ) {

            readMap ( lmap, args[0] );

            final String hostname = args[1];

            javax.swing.SwingUtilities.invokeLater ( new Runnable() {
                public void run() {

                    java.util.Map.Entry<Long, Loc> e = lmap.entrySet().iterator().next();

                    new CarWindow ( e.getValue().lat, e.getValue().lon, lmap, hostname, 10007 ).setVisible ( true );
                }
            } );

        } else if ( args.length == 3 ) {

            readMap ( lmap, args[0] );

            final String hostname = args[1];
            final int port = Integer.parseInt ( args[2] );

            javax.swing.SwingUtilities.invokeLater ( new Runnable() {
                public void run() {

                    java.util.Map.Entry<Long, Loc> e = lmap.entrySet().iterator().next();

                    new CarWindow ( e.getValue().lat, e.getValue().lon, lmap, hostname, port ).setVisible ( true );
                }
            } );

        } else {

            System.out.println ( "java -jar target/site/justine-rcwin-0.0.16-jar-with-dependencies.jar ../../../lmap.txt localhost" );
        }

    }

}

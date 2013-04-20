package edu.gmu.vfml.test;

import com.metsci.glimpse.examples.Example;
import com.metsci.glimpse.support.repaint.RepaintManager;

import weka.classifiers.trees.CVFDT;
import weka.core.Instance;
import edu.gmu.vfml.data.BooleanConcept;
import edu.gmu.vfml.data.RandomDataGenerator;
import edu.gmu.vfml.ui.TreeVisualization;
import edu.gmu.vfml.ui.VisualizableNode;

public class TestStream
{
    public static void main( String[] args ) throws Exception
    {
        // define a boolean concept
        BooleanConcept concept = new BooleanConcept( )
        {
            @Override
            public boolean f( boolean[] v )
            {
                return ( v[1] && ( v[5] || v[6] || !v[12] ) && !v[7] ) || ( v[3] && ( !v[2] || v[4] ) && v[12] ) || ( v[6] && v[12] );
            }
        };

        // build a random data generator using the concept
        // (which randomly flips the class value 5% of the time)
        RandomDataGenerator generator = new RandomDataGenerator( concept, 15, 0.05 );

        // build a CVFDT classifier
        final CVFDT classifier = new CVFDT( );
        classifier.setConfidenceLevel( 1e-6 );
        classifier.setNMin( 30 );
        classifier.initialize( generator.getDataset( ) );

        final TreeVisualization visualization = new TreeVisualization( );
        final Example example = Example.showWithSwing( visualization );
        final RepaintManager repaintManager = example.getManager( );
        
        for ( int i = 0;; i++ )
        {
            Instance instance = generator.next( );
            classifier.addInstance( instance );

            if ( i % 200 == 0 )
            {
                // make a copy of the current classifier tree structure
                final VisualizableNode vNode = VisualizableNode.copyTree( classifier.getRoot( ) );
                
                repaintManager.syncExec( new Runnable( )
                {
                    @Override
                    public void run( )
                    {
                        visualization.setNode( vNode );
                    }
                });
            }
        }
    }
}
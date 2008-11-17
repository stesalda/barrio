package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;

public class OutputUI extends Composite{
	
	private Tree matrixCluster;
	private JTable table;
	private boolean showSingletons;
	
	public OutputUI(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.showSingletons = true;
		GridData tabData = new GridData(GridData.FILL_BOTH);
		
		final TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
	    TabItem itemMatrix = new TabItem(tabFolder, SWT.NONE);
	    itemMatrix.setText("Clusters");
	    TabItem itemRE = new TabItem(tabFolder, SWT.NONE);
	    itemRE.setText("Removed edges");
	    tabFolder.setLayoutData(tabData);
	    
	    matrixCluster = new Tree(tabFolder, SWT.BORDER);
	    matrixCluster.setHeaderVisible(true);
	    matrixCluster.setLinesVisible(true);
	    itemMatrix.setControl(matrixCluster);
	    
		
		Composite edges = new Composite(tabFolder, SWT.EMBEDDED);
		Frame frameEdges = SWT_AWT.new_Frame(edges);
		Panel panelEdges = new Panel(new BorderLayout());
		frameEdges.add(panelEdges);
		DefaultTableModel model = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;

			public Class<String> getColumnClass(int columnIndex) {
		          return String.class;
		      }
		};
		model.setDataVector(null, new Object[]{"id","","Source class","Relationship","Destination class","Betweenness"});
		table = new JTable(model){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column){
				return column==6;
			}
		};
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowHeight( table.getRowHeight() * 3 + 5);
		TableColumn srcColumn = table.getColumnModel().getColumn(2);
		srcColumn.setCellRenderer(new MultiLineCellRenderer());
		TableColumn destColumn = table.getColumnModel().getColumn(4);
		destColumn.setCellRenderer(new MultiLineCellRenderer());
		
		
		{
			table.getTableHeader().setReorderingAllowed(false);
			
			TableColumn col0 = table.getColumnModel().getColumn(0);
		    col0.setMinWidth(0);
		    col0.setMaxWidth(0);
		    col0.setPreferredWidth(0);
			
			TableColumn col1 = table.getColumnModel().getColumn(1);
		    col1.setMinWidth(10);
		    col1.setMaxWidth(100);
		    col1.setPreferredWidth(40);
		    
		    TableColumn col3 = table.getColumnModel().getColumn(3);
		    col3.setMinWidth(100);
		    col3.setMaxWidth(150);
		    col3.setPreferredWidth(125);
		    
		    TableColumn col5 = table.getColumnModel().getColumn(5);
		    col5.setMinWidth(70);
		    col5.setMaxWidth(110);
		    col5.setPreferredWidth(85);
		}
		JScrollPane scrollPane = new JScrollPane(table);
		panelEdges.add(scrollPane);
		itemRE.setControl(edges);
	} 
		
	public void dispose() {
		super.dispose();
	}

	public void updateOutputs(Graph graph) 
	{        
		OutputGenerator og = new OutputGenerator();
		og.generateTableTree(graph, matrixCluster, showSingletons);
	}
	
	public void updateTableRemovedEdges(List<Edge> removedEdges)
	{
		OutputGenerator og = new OutputGenerator();
		List<Object[]> list = new ArrayList<Object[]>();
		og.generateListRemovedEdges(list, removedEdges);
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		for(int i=dtm.getRowCount()-1; i>-1; i--) dtm.removeRow(i);
        for(Object[] edgeData:list) dtm.addRow(edgeData);
	}

	public Tree getMatrixCluster() {
		return matrixCluster;
	}

	public void setShowSingletons(boolean showSingletons) {
		this.showSingletons = showSingletons;
	}
}

package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import nz.ac.massey.cs.barrio.visual.VisualHighlightingManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

import prefuse.Display;
import prefuse.data.Edge;
import prefuse.data.tuple.TupleSet;

public class OutputUI extends Composite{
	
	private Tree treeProject;
	private Tree treePwMC;
	private Tree treeCwMP;
	private Panel panelGraph;
	private JTable table;
	
	public OutputUI(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		GridData tabData = new GridData(GridData.FILL_BOTH);
		
		final TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
		TabItem itemProject = new TabItem(tabFolder, SWT.NONE);
	    itemProject.setText("Project description");
	    TabItem itemPwMC = new TabItem(tabFolder, SWT.NONE);
	    itemPwMC.setText("Packages with multiple clusters");
	    TabItem itemCwMP = new TabItem(tabFolder, SWT.NONE);
	    itemCwMP.setText("Clusters with multiple packages");
	    TabItem itemRE = new TabItem(tabFolder, SWT.NONE);
	    itemRE.setText("Removed edges");
	    final TabItem itemGraph = new TabItem(tabFolder, SWT.NONE);
	    itemGraph.setText("Dependency graph");
	    tabFolder.setLayoutData(tabData);
	    
	    treeProject = new Tree(tabFolder, SWT.BORDER | SWT.MULTI);
	    itemProject.setControl(treeProject);
	    
	    treePwMC = new Tree(tabFolder, SWT.BORDER | SWT.MULTI);
		itemPwMC.setControl(treePwMC);
		
		treeCwMP = new Tree(tabFolder, SWT.BORDER | SWT.MULTI);
		itemCwMP.setControl(treeCwMP);
		
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
		model.setDataVector(null, new Object[]{"id","","Source class","Relationship","Destination class","Betweenness",""});
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
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				highlightEdges();
			}
			
		});
		checkBox.setVisible(true);
		TableColumn lastColumn = table.getColumnModel().getColumn(6);
		lastColumn.setCellEditor(new DefaultCellEditor(checkBox));
		lastColumn.setCellRenderer(new CheckBoxRenderer());
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
		    
		    TableColumn col6 = table.getColumnModel().getColumn(6);
		    col6.setMinWidth(20);
		    col6.setMaxWidth(30);
		    col6.setPreferredWidth(25);
		}
		JScrollPane scrollPane = new JScrollPane(table);
		panelEdges.add(scrollPane);
		itemRE.setControl(edges);
		
		Composite graphVis = new Composite(tabFolder, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(graphVis);
		frame.setLayout(new BorderLayout());
		panelGraph = new Panel(new BorderLayout()){  
			private static final long serialVersionUID = 1L;
			public void update(java.awt.Graphics g){  
				/* Do not erase the background */  
				paint(g);  
			}  
		}; 
		
		frame.add(panelGraph);
		itemGraph.setControl(graphVis);
		tabFolder.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				graphFolderSelected(tabFolder, itemGraph);
				
			}
			
		});
	}
	
	
	protected void graphFolderSelected(TabFolder tabFolder, TabItem itemGraph) 
	{
		GuiGetter gg = new GuiGetter();
		InputUI input = gg.getInputUI();
		if(tabFolder.getSelectionIndex()==tabFolder.indexOf(itemGraph))
			input.setGraphControlsCompositeVisible(true);
		else input.setGraphControlsCompositeVisible(false);
		
	}


	private void updateTable(java.util.List<Object[]> list)
	{
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		for(int i=dtm.getRowCount()-1; i>-1; i--) dtm.removeRow(i);
		for(Object[] edgeData:list) dtm.addRow(edgeData);
	}
	
	
	private void highlightEdges()
	{
		String edgeId = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
		String highlight = table.getModel().getValueAt(table.getSelectedRow(), 6).toString();
		
		setEdge(edgeId, highlight);	
	}
	
	
	@SuppressWarnings("unchecked")
	private void setEdge(String edgeId, String value)
	{
		Display dis = (Display) panelGraph.getComponent(0);
		if(dis!=null){
			TupleSet edges = dis.getVisualization().getGroup("graph.edges");
			Iterator iter = edges.tuples();
			while(iter.hasNext())
			{
				Edge edge = (Edge)iter.next();
				if(edgeId.equals(edge.get("id")))
				{
					highlightVisual(dis, edge, value);
					break;
				}
				
			}
		}
	}
	
	
	private void highlightVisual(Display dis, Edge edge, String select)
	{
		VisualHighlightingManager mgr = new VisualHighlightingManager();
		mgr.setSelectItem(dis.getVisualization().getVisualItem("graph.edges", edge), select);
	}
	
	
	
	public void dispose() {
		super.dispose();
	}
	
	public void paintGraph(Component comp)
	{
		panelGraph.removeAll();
		panelGraph.add(comp, 0);
		panelGraph.doLayout();
		panelGraph.repaint();
	}
	
	public Component getGraphComponent()
	{
		return panelGraph.getComponent(0);
	}


	@SuppressWarnings("unchecked")
	public void updateOutputs(OutputGenerator og, List edges) 
	{
		System.out.println("[OutputUI]: updateOutputs called");
		og.generateProjectDescription(treeProject);
		og.generatePackagesWithMultipleClusters(treePwMC);
        og.generateClustersWithMuiltiplePackages(treeCwMP);
        
        List<Object[]> list = new ArrayList<Object[]>();
        og.generateListRemovedEdges(list, edges);
        updateTable(list);
	}


	public JTable getTable() {
		return table;
	}


	public Tree getTreePwMC() {
		return treePwMC;
	}


	public Tree getTreeCwMP() {
		return treeCwMP;
	}

}

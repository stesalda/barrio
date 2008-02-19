package nz.ac.massey.cs.barrio.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;

import prefuse.Display;
import prefuse.visual.AggregateItem;

public class OutputUI extends Composite{
	
	public static Tree treeProject;
	public static Tree treePwMC;
	public static Tree treeCwMP;
	public static Panel panelGraph;
	
	private static JTable table;
	
	public OutputUI(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		GridData tabData = new GridData(GridData.FILL_BOTH);
		
		TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
		TabItem itemProject = new TabItem(tabFolder, SWT.NONE);
	    itemProject.setText("Project description");
	    TabItem itemPwMC = new TabItem(tabFolder, SWT.NONE);
	    itemPwMC.setText("Packages with multiple clusters");
	    TabItem itemCwMP = new TabItem(tabFolder, SWT.NONE);
	    itemCwMP.setText("Clusters with multiple packages");
	    TabItem itemRE = new TabItem(tabFolder, SWT.NONE);
	    itemRE.setText("Removed edges");
	    TabItem itemGraph = new TabItem(tabFolder, SWT.NONE);
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
		      public Class getColumnClass(int columnIndex) {
		          return String.class;
		      }
		};
		model.setDataVector(null, new Object[]{"","Source class","Relationship","Destination class","Betweenness"});
		table = new JTable(model){
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		table.setRowHeight( table.getRowHeight() * 3 + 5);
		table.setDefaultRenderer(String.class, new MultiLineCellRenderer());
		{
			table.getTableHeader().setReorderingAllowed(false);
			javax.swing.table.TableColumn col0 = table.getColumnModel().getColumn(0);
		    col0.setMinWidth(10);
		    col0.setMaxWidth(100);
		    col0.setPreferredWidth(40);
		    
		    javax.swing.table.TableColumn col2 = table.getColumnModel().getColumn(2);
		    col2.setMinWidth(100);
		    col2.setMaxWidth(150);
		    col2.setPreferredWidth(125);
		    
		    javax.swing.table.TableColumn col4 = table.getColumnModel().getColumn(4);
		    col4.setMinWidth(70);
		    col4.setMaxWidth(110);
		    col4.setPreferredWidth(85);
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
		Panel controls = new Panel(null);
		controls.setBounds(5, 25, 120,60);
		final JCheckBox checkJars = new JCheckBox("View Jars");
		checkJars.setBounds(0,0,120,20);
		checkJars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkJarActionPerformed(evt, checkJars.isSelected());
			}
		});
		final JCheckBox checkPacks = new JCheckBox("View Packages");
		checkPacks.setBounds(0,20,120,20);
		checkPacks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkPacksActionPerformed(evt, checkPacks.isSelected());
			}
		});
		final JCheckBox checkClusters = new JCheckBox("View Clusters");
		checkClusters.setBounds(0,40,120,20);
		checkClusters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkClustersActionPerformed(evt, checkClusters.isSelected());
			}
		});
		controls.add(checkJars);
		controls.add(checkPacks);
		controls.add(checkClusters);
		frame.add(controls);
		frame.add(panelGraph);
		itemGraph.setControl(graphVis);
	}
	
	
	protected void checkPacksActionPerformed(ActionEvent evt, boolean b) {
		Display dis = (Display) panelGraph.getComponent(0);
		if(dis!=null)
		{
			Iterator i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("package") && b) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("package") && !b) ai.setVisible(false);
			}
		}
	}


	protected void checkClustersActionPerformed(ActionEvent evt, boolean b) {
		Display dis = (Display) panelGraph.getComponent(0);
		if(dis!=null)
		{
			Iterator i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("cluster") && b) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("cluster") && !b) ai.setVisible(false);
			}
		}		
	}


	protected void checkJarActionPerformed(ActionEvent evt, boolean b) 
	{
		Display dis = (Display) panelGraph.getComponent(0);
		if(dis!=null)
		{
			Iterator i = dis.getVisualization().getVisualGroup("aggregates").tuples();
			while(i.hasNext())
			{
				AggregateItem ai = ((AggregateItem)i.next());
				if (ai.get("type")!=null && ai.getString("type").equals("jar") && b) ai.setVisible(true);
				if (ai.get("type")!=null && ai.getString("type").equals("jar") && !b) ai.setVisible(false);
			}
		}
	}


	protected void checkboxClick() {
		// TODO Auto-generated method stub
		
	}


	public static void updateTable(java.util.List<String[]> list)
	{
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		
		for(int i=dtm.getRowCount()-1; i>-1; i--)
		{
			dtm.removeRow(i);
		}
		
		Iterator<String[]> iter = list.iterator();		
		while (iter.hasNext()) dtm.addRow(iter.next());
	}
	
	public void dispose() {
		super.dispose();
	}

}

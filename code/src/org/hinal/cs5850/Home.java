package org.hinal.cs5850;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.io.FileUtils;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.CardLayout;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.MultiHashMap;

public class Home {

	private JFrame frmTestGeneration;
	public JTextField txtFilePath;
	JPanel pnlMethodConfig;
	CardLayout card;
	JButton btnGenerateTestFile,btnAddTest;
	private static Class<?>  cls;
	static DefaultTableModel model,modelParams, modelExe,modelParamsPnlSecond,modelAssert;
	static HashMap<Integer,Method> hmMethod=new HashMap<Integer,Method>();
	static HashMap<Integer,String> staticMethodHM=new HashMap<Integer,String>();
	static HashMap<Integer,ArrayList<Parameter>> paramsHM=new HashMap<Integer, ArrayList<Parameter>>();
	static HashMap<String,MultiMap> testParamsHM=new HashMap<String,MultiMap>();
	static MultiMap testHashMap= new MultiHashMap();
	static HashMap<String,ArrayList<Object>> constructosParamsHM= new HashMap<String,ArrayList<Object>>();
	static HashMap<String,ArrayList<Object>> assertStatementsHM= new HashMap<String,ArrayList<Object>>();
	public JTextField txtPackageName;
	static  JTextField txtTestName;
	static int methodSelectedID=-1;
	public JTable tblMethodList,tbl;
	static String selectedTestName=null;
	public String strclassName="";
	public File prevFilePath=null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home window = new Home();
					window.frmTestGeneration.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Home() {
		initialize();
	}

	public void initialize() {
		String title="";
		Border border=null;
		String classDirPath="";
		
		frmTestGeneration = new JFrame();
		frmTestGeneration.getContentPane().setBackground(Color.WHITE);
				
		frmTestGeneration.setUndecorated( true );
		frmTestGeneration.setTitle("Test Generation");
		UIManager.put("JFrame.activeTitleBackground", Color.red);
		frmTestGeneration.setResizable(false);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frmTestGeneration.setSize(screenSize.width, screenSize.height-35);
		frmTestGeneration.setVisible(true);
		frmTestGeneration.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTestGeneration.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 51, 153));
		panel.setBounds(12, 39, 1896, 94);
		frmTestGeneration.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblAutoTestGenerator = new JLabel("Auto Test Generator");
		lblAutoTestGenerator.setForeground(Color.WHITE);
		lblAutoTestGenerator.setFont(new Font("Calibri", Font.BOLD, 40));
		lblAutoTestGenerator.setBounds(12, 13, 755, 56);
		panel.add(lblAutoTestGenerator);
		
		JButton btnClose = new JButton("X");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnClose.setBackground(new Color(255, 0, 0));
		btnClose.setOpaque(true);
		btnClose.setBounds(1852, 13, 56, 25);
		frmTestGeneration.getContentPane().add(btnClose);
		
		JLabel lblPleaseSelectJava = new JLabel("Select java class file (.class) :");
		lblPleaseSelectJava.setFont(new Font("Calibri", Font.BOLD, 25));
		lblPleaseSelectJava.setForeground(new Color(0, 51, 153));
		lblPleaseSelectJava.setBounds(140, 160, 304, 34);
		frmTestGeneration.getContentPane().add(lblPleaseSelectJava);
		
		txtFilePath = new JTextField();
		txtFilePath.setFont(new Font("Calibri", Font.PLAIN, 18));
		txtFilePath.setEditable(false);
		txtFilePath.setBounds(446, 165, 993, 28);
		frmTestGeneration.getContentPane().add(txtFilePath);
		txtFilePath.setColumns(10);
		
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnGenerateTestFile.setEnabled(false);
				btnAddTest.setEnabled(false);	    
							
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView());
				jfc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".class File", "class");
				jfc.addChoosableFileFilter(filter);
				
				int returnValue = jfc.showOpenDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					txtFilePath.setText(selectedFile.getAbsolutePath());
				}
				
				//clear things
				hmMethod.clear();
	        	paramsHM.clear();
	        	testHashMap.clear();
	        	testParamsHM.clear();
	        	assertStatementsHM.clear();
	        	model.setRowCount(0);
	        	modelParams.setRowCount(0);
	        	modelExe.setRowCount(0);
	        	modelParamsPnlSecond.setRowCount(0);
	        	modelAssert.setRowCount(0);
	        	staticMethodHM.clear();
			}
		});
		btnBrowse.setBackground(new Color(0, 51, 153));
		btnBrowse.setForeground(Color.WHITE);
		btnBrowse.setFont(new Font("Calibri", Font.BOLD, 20));
		btnBrowse.setOpaque(true);
		btnBrowse.setBounds(1451, 167, 97, 25);
		frmTestGeneration.getContentPane().add(btnBrowse);
		
		txtPackageName = new JTextField();
		txtPackageName.setFont(new Font("Calibri", Font.PLAIN, 18));
		txtPackageName.setColumns(10);
		txtPackageName.setBounds(446, 205, 554, 28);
		frmTestGeneration.getContentPane().add(txtPackageName);
		
			
		JPanel pnlInfo = new JPanel();
		pnlInfo.setBackground(new Color(0, 51, 153));
		pnlInfo.setBounds(66, 246, 471, 668);
		frmTestGeneration.getContentPane().add(pnlInfo);
		pnlInfo.setLayout(null);
		
		JPanel pnlClassInfo = new JPanel();
		pnlClassInfo.setBounds(12, 13, 447, 179);
		title = "Class Information";
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
		pnlClassInfo.setBorder(border);
		pnlInfo.add(pnlClassInfo);
		pnlClassInfo.setLayout(null);
		
		JLabel lblClassName = new JLabel("Class Name:");
		lblClassName.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblClassName.setBounds(12, 47, 109, 23);
		pnlClassInfo.add(lblClassName);
		
		JLabel lblPackageName = new JLabel("Package Name:");
		lblPackageName.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblPackageName.setBounds(12, 83, 131, 23);
		pnlClassInfo.add(lblPackageName);
		
		JLabel lblClassModifier = new JLabel("Class Modifier:");
		lblClassModifier.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblClassModifier.setBounds(12, 119, 131, 23);
		pnlClassInfo.add(lblClassModifier);
		
		JLabel lblClassNameValue = new JLabel("");
		lblClassNameValue.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblClassNameValue.setBounds(116, 50, 319, 16);
		pnlClassInfo.add(lblClassNameValue);
		
		JLabel lblPackageValue = new JLabel("");
		lblPackageValue.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblPackageValue.setBounds(142, 86, 293, 16);
		pnlClassInfo.add(lblPackageValue);
		
		JLabel lblModifierValue = new JLabel("");
		lblModifierValue.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblModifierValue.setBounds(142, 119, 293, 16);
		pnlClassInfo.add(lblModifierValue);
		
		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(txtFilePath.getText().trim().length()>0 && txtPackageName.getText().trim().length()>0)
				{
						
				String path=txtFilePath.getText();
		        String last=path.substring(path.lastIndexOf("\\") + 1);
		        last=last.substring(last.lastIndexOf(".")+1);
		        
		        if (("class").equals(last))
		        {
		        	
		        	hmMethod.clear();
		        	paramsHM.clear();
		        	testHashMap.clear();
		        	testParamsHM.clear();
		        	assertStatementsHM.clear();
		        	model.setRowCount(0);
		        	modelParams.setRowCount(0);
		        	modelExe.setRowCount(0);
		        	modelParamsPnlSecond.setRowCount(0);
		        	modelAssert.setRowCount(0);
		        	staticMethodHM.clear();
		        	btnAddTest.setEnabled(false);
		        	 try{
		        		 if(prevFilePath!=null)
		        		 {
		        			 if (prevFilePath.exists())
		        			 {
		        				 //delete temporary file if exists
		        				   deleteDirectory(prevFilePath);
		        			 }
		        		 }
		        		  cls=null;
		        		  cls=loadClassAndGetClassName(txtFilePath.getText(),txtPackageName.getText());
		        		if (cls!=null)
		        		{
		        		 btnGenerateTestFile.setEnabled(true);
		        		 lblClassNameValue.setText(cls.getSimpleName());
		        		 lblPackageValue.setText(cls.getPackage().getName());
		        		 
		        		 
		        		 int m= cls.getModifiers();
		        		 String mVal=getModifierName(m,-1);
		        		 lblModifierValue.setText(mVal);
		        		 getMethodAndParametersInfo();
		        		 if (hmMethod.size() !=0)
		        		 {
		        			 for (int count = 0; count < hmMethod.size(); count++) {
		        				 if(hmMethod.get(count).getReturnType().toString().equals("class java.lang.String"))
		        				 {
		        					 model.addRow(new Object[] { hmMethod.get(count).getName(),getModifierName(hmMethod.get(count).getModifiers(),count)
				        					 , "String"});
		        				 }
		        				 else
		        				 {
		        					 model.addRow(new Object[] { hmMethod.get(count).getName(),getModifierName(hmMethod.get(count).getModifiers(),count)
		        					 , hmMethod.get(count).getReturnType()});
		        				 }
		        			 }
		        		 }
		        		 makeDefaultTestForAllMethods();
		        		}
		        	 }catch(Exception ex){
		        		 btnGenerateTestFile.setEnabled(false);
		        		ex.printStackTrace();
		        	}
		        }
		        else
		        {
		        	JOptionPane.showMessageDialog(frmTestGeneration, "Invalid File");
		        }
				
				}
				else
				{
					if(txtFilePath.getText().trim().length()==0)
					{
						JOptionPane.showMessageDialog(frmTestGeneration, "Please select file");
					}
					else if (txtPackageName.getText().trim().length()==0) {
						
						JOptionPane.showMessageDialog(frmTestGeneration, "Please enter package name");
					}
				}
			}
		});
		btnGo.setOpaque(true);
		btnGo.setForeground(Color.WHITE);
		btnGo.setFont(new Font("Calibri", Font.BOLD, 20));
		btnGo.setBackground(new Color(0, 51, 153));
		btnGo.setBounds(1012, 206, 70, 25);
		frmTestGeneration.getContentPane().add(btnGo);
		
				    
		JPanel pnlMethodList = new JPanel();
		pnlMethodList.setBounds(12, 205, 447, 450);
		title= "List Of Methods";
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
		pnlMethodList.setBorder(border);
		pnlInfo.add(pnlMethodList);
		
		
		model = new DefaultTableModel(){
				boolean[] canEdit = new boolean[]{
	                    false, false, false
	            };

	           public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit[columnIndex];
	            }
	};
        model.addColumn("Method Name");
        model.addColumn("Modifier");
        model.addColumn("Return Type");
        
                
        tblMethodList = new JTable(model);
        
        TableCellRenderer renderer = new EvenOddRenderer();
        tblMethodList.setDefaultRenderer(Object.class, renderer);
        
        TableColumnModel columnModel= tblMethodList.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(140);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(160);
        tblMethodList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblMethodList.setRowHeight(30);
        tblMethodList.setBackground(Color.WHITE);
        tblMethodList.setFont(new Font("Callibri", Font.PLAIN, 16));
        
      
        JScrollPane scrollPane2 = new JScrollPane(tblMethodList); 
        scrollPane2.setPreferredSize(new Dimension(420, 410));
        pnlMethodList.add(scrollPane2);
        
        
        pnlMethodConfig = new JPanel();
		pnlMethodConfig.setBounds(536, 246, 1313, 668);
		frmTestGeneration.getContentPane().add(pnlMethodConfig);
		card = new CardLayout(0, 0);
		pnlMethodConfig.setLayout(card);
		
		
		JPanel pnlFirst = new JPanel();
		pnlFirst.setBounds(536, 246, 1313, 668);
		pnlMethodConfig.add(pnlFirst);
		pnlFirst.setLayout(null);
		
		JPanel pnlChild1 = new JPanel();
		pnlChild1.setBounds(36, 37, 1265, 618);
		title= "Method Information";
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
		pnlChild1.setBorder(border);
		pnlChild1.setLayout(null);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblName.setBounds(25, 31, 64, 23);
		pnlChild1.add(lblName);
		
		JLabel lblReturnType = new JLabel("Return Type:");
		lblReturnType.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblReturnType.setBounds(812, 31, 101, 23);
		pnlChild1.add(lblReturnType);
		
		JLabel lblParamNames = new JLabel("Parameter List:");
		lblParamNames.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblParamNames.setBounds(25, 99, 125, 23);
		pnlChild1.add(lblParamNames);
		
		JLabel lblModifier = new JLabel("Modifier:");
		lblModifier.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblModifier.setBounds(519, 31, 101, 23);
		pnlChild1.add(lblModifier);
		
		modelParams = new DefaultTableModel()
		{
			boolean[] canEdit = new boolean[]{
                    false, false
            };

           public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
		};
		modelParams.addColumn("Parameter Name");
		modelParams.addColumn("Data Type");
		
		JTable tblParams = new JTable(modelParams);
		
        TableCellRenderer render = new EvenOddRenderer();
        tblParams.setDefaultRenderer(Object.class, render);
        
        TableColumnModel colModel= tblParams.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(250);
        colModel.getColumn(1).setPreferredWidth(240);
        
        tblParams.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblParams.setRowHeight(30);
        tblParams.setBackground(Color.WHITE);
        tblParams.setFont(new Font("Callibri", Font.PLAIN, 16));
           
        pnlFirst.add(pnlChild1);
        
        JScrollPane scrollPane = new JScrollPane(tblParams);
        scrollPane.setBounds(168, 93, 515, 169);
        pnlChild1.add(scrollPane);
        
        JLabel lblNumberOfTest = new JLabel("Number of Tests:");
        lblNumberOfTest.setFont(new Font("Calibri", Font.PLAIN, 18));
        lblNumberOfTest.setBounds(25, 321, 138, 16);
        pnlChild1.add(lblNumberOfTest);
        
    	modelExe = new DefaultTableModel()
    	{
			boolean[] canEdit = new boolean[]{
                    false, true,true
            };

           public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
		};
    	modelExe.addColumn("Tests");
    	modelExe.addColumn("Edit");
    	modelExe.addColumn("Delete");
		
		JTable tblExecutionPath = new JTable(modelExe);
				
        TableCellRenderer render1 = new EvenOddRenderer();
        tblExecutionPath.setDefaultRenderer(Object.class, render1);
        
        TableColumnModel colModel1= tblExecutionPath.getColumnModel();
        colModel1.getColumn(0).setPreferredWidth(390);
        colModel1.getColumn(1).setPreferredWidth(100);
        colModel1.getColumn(2).setPreferredWidth(100);
        
        tblExecutionPath.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblExecutionPath.setRowHeight(30);
        tblExecutionPath.setBackground(Color.WHITE);
        tblExecutionPath.setFont(new Font("Callibri", Font.PLAIN, 16));
        
        ButtonColumn buttonColumn = new ButtonColumn(tblExecutionPath, 1,"tblExecutionPath");
        ButtonColumn buttonColumn1 = new ButtonColumn(tblExecutionPath, 2,"tblExecutionPath");
        JScrollPane scrollPane_1 = new JScrollPane(tblExecutionPath);
        scrollPane_1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_1.setBounds(168, 325, 609, 169);
        pnlChild1.add(scrollPane_1);
        
        JLabel lblNameValue = new JLabel("");
        lblNameValue.setFont(new Font("Calibri", Font.PLAIN, 18));
        lblNameValue.setBounds(82, 31, 379, 23);
        pnlChild1.add(lblNameValue);
        
        JLabel lblRetValue = new JLabel("");
        lblRetValue.setFont(new Font("Calibri", Font.PLAIN, 18));
        lblRetValue.setBounds(913, 31, 327, 23);
        pnlChild1.add(lblRetValue);
        
        JLabel lblModValue = new JLabel("");
        lblModValue.setFont(new Font("Calibri", Font.PLAIN, 18));
        lblModValue.setBounds(596, 31, 204, 23);
        pnlChild1.add(lblModValue);
		
        btnAddTest = new JButton("Add Test");
        btnAddTest.setOpaque(true);
        btnAddTest.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		btnGenerateTestFile.setEnabled(false);
        		tblMethodList.setEnabled(false);
        		tblMethodList.setCellSelectionEnabled(false);
        		modelParamsPnlSecond.setRowCount(0);
        		modelAssert.setRowCount(0);
        		txtTestName.setText("");
        		txtTestName.setEnabled(true);
        		ArrayList<Parameter> arr_params=paramsHM.get(methodSelectedID);
		   	      
		   	      for (int i=0;i<arr_params.size();i++)
		   	      {
		   	    	String paramType="",paramDefaultVal="";
		   	    	if(arr_params.get(i).getParameterizedType().getTypeName().equals("java.lang.String[]"))
					{
		   	    		paramType="String Array";
		   	    		paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("int[]"))
					{
						paramType="int Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("float[]"))
					{
						paramType="float Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("long[]"))
					{
						paramType="long Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("double[]"))
					{
						paramType="double Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("short[]"))
					{
						paramType="short Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("byte[]"))
					{
						paramType="byte Array";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().equals("boolean[]"))
					{
						paramType="boolean Array";
						paramDefaultVal="null";
					}
					else if (arr_params.get(i).getParameterizedType().getTypeName().contains("int"))
					{
						paramType="int";
						paramDefaultVal="0";
						
					}
					else if( arr_params.get(i).getParameterizedType().getTypeName().contains("long") )
					{
						paramType="long";
						paramDefaultVal="0";
					
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("float"))
					{
						paramType="float";
						paramDefaultVal="0.0";
						
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("double") )
					{
						paramType="double";
						paramDefaultVal="0.0";
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("short") )
					{
						paramType="short";
						paramDefaultVal="0";
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("byte"))
					{
						paramType="byte";
						paramDefaultVal="0";
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("String") )
					{
						paramType="String";
						paramDefaultVal="null";
						
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("java.class.String"))
					{
						paramType="String";
						paramDefaultVal="null";
					}
					else if(arr_params.get(i).getParameterizedType().getTypeName().contains("boolean") )
					{
						paramType="boolean";
						paramDefaultVal="false";
						
					}
					modelParamsPnlSecond.addRow(new Object[] { arr_params.get(i).getName(),paramType, paramDefaultVal });
					
		   	    	
		   	      }
        		
        		card.next(pnlMethodConfig);
        	}
        });
        
        btnAddTest.setForeground(Color.WHITE);
        btnAddTest.setFont(new Font("Calibri", Font.BOLD, 20));
        btnAddTest.setBackground(new Color(0, 51, 153));
        btnAddTest.setBounds(168, 507, 125, 25);
        pnlChild1.add(btnAddTest);
       
        JPanel pnlSecond = new JPanel();
		pnlSecond.setBounds(536, 246, 1313, 668);
		pnlMethodConfig.add(pnlSecond);
		title= "Execution Path Configuration";
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
		
		pnlMethodConfig.add(pnlSecond);
		pnlSecond.setLayout(null);
        
		tblMethodList.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent evt) {
			 if (evt.getClickCount() == 2) {
		   	      JTable target = (JTable)evt.getSource();
		   	      int row = target.getSelectedRow();
		   	      modelParams.setRowCount(0);
		   	      modelExe.setRowCount(0);
		   	      methodSelectedID=row;
		   	      modelAssert.setRowCount(0);
		   	      btnAddTest.setEnabled(true);
		   	      //JOptionPane.showMessageDialog(frmTestGeneration, "Row id: " + row);
		   	      lblNameValue.setText(target.getValueAt(row,  0).toString());
		   	      lblModValue.setText(target.getValueAt(row,  1).toString());
		   	      lblRetValue.setText(target.getValueAt(row,  2).toString());
		   	      
		   	      ArrayList<Parameter> arr_params=paramsHM.get(row);
		   	      
		   	      for (int i=0;i<arr_params.size();i++)
		   	      {
		   	    	
		   	    	if (arr_params.get(i).getParameterizedType().getTypeName().equals("java.lang.String[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"String Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("int[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"int Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("float[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"float Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("double[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"double Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("long[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"long Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("short[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"short Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("byte[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"byte Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().equals("boolean[]"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"boolean Array" });
		   	    	}
		   	    	else if (arr_params.get(i).getParameterizedType().getTypeName().contains("java.lang.String"))
		   	    	{
		   	    		modelParams.addRow(new Object[] { arr_params.get(i).getName(),"String" });
		   	    	}
		   	    	else{
		   	    	modelParams.addRow(new Object[] { arr_params.get(i).getName(),arr_params.get(i).getType() });
		   	    	}
		   	    	
		   	      }
		   	      
		   	      //########################################## fill default test #############################
		   	      Set keySet = testHashMap.keySet( );
		   	      Iterator keyIterator = keySet.iterator();
			 
		   	      while( keyIterator.hasNext( ) ) {
			        int key =(Integer) keyIterator.next( );
			        if(key==row)
			        {
			        	Collection values = (Collection)testHashMap.get(key);
			        	Iterator valuesIterator = values.iterator( );
			        	while( valuesIterator.hasNext( ) ) {
			        		modelExe.addRow(new Object[]{valuesIterator.next()});
			        	}
			        }
			        
		   	      }
		   	      
			 }
        	}});
		
//##################################################################################################################################################################
		JPanel pnlChild2 = new JPanel();
		pnlChild2.setBounds(23, 24, 1265, 631);
		title= "Method Information";
		border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
		pnlChild2.setBorder(border);
		pnlChild2.setLayout(null);
		pnlSecond.add(pnlChild2);
		
		JLabel lblTestName = new JLabel("Test Name:");
		lblTestName.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblTestName.setBounds(23, 26, 145, 28);
		pnlChild2.add(lblTestName);
		
		txtTestName = new JTextField();
		txtTestName.setFont(new Font("Calibri", Font.PLAIN, 18));
		txtTestName.setBounds(198, 29, 522, 22);
        pnlChild2.add(txtTestName);
        txtTestName.setColumns(20);
        txtTestName.setEnabled(false);
        
		JLabel lblParamValues = new JLabel("Set Parameter Values:");
		lblParamValues.setFont(new Font("Calibri", Font.PLAIN, 18));
		lblParamValues.setBounds(23, 67, 167, 23);
		pnlChild2.add(lblParamValues);
		
		JLabel lblParamInfo = new JLabel("Use '|' (pipe) as seperator in case of array type parameters");
        lblParamInfo.setFont(new Font("Calibri", Font.PLAIN, 16));
        lblParamInfo.setBounds(566, 239, 394, 23);
        pnlChild2.add(lblParamInfo);
		
		modelParamsPnlSecond = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;
				boolean[] canEdit = new boolean[]{
		                    false, false, true
		            };

		           public boolean isCellEditable(int rowIndex, int columnIndex) {
		                return canEdit[columnIndex];
		            }
		};
		modelParamsPnlSecond.addColumn("Parameter Name");
		modelParamsPnlSecond.addColumn("Data Type");
		modelParamsPnlSecond.addColumn("Enter Value");
		
		JTable tblParamsValues = new JTable(modelParamsPnlSecond);
		
        TableCellRenderer renderPnlSecond = new EvenOddRenderer();
        tblParamsValues.setDefaultRenderer(Object.class, renderPnlSecond);
        
        TableColumnModel colModelPnlSecond= tblParamsValues.getColumnModel();
        colModelPnlSecond.getColumn(0).setPreferredWidth(250);
        colModelPnlSecond.getColumn(1).setPreferredWidth(240);
        colModelPnlSecond.getColumn(2).setPreferredWidth(250);
        
        tblParamsValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblParamsValues.setRowHeight(30);
        tblParamsValues.setBackground(Color.WHITE);
        tblParamsValues.setFont(new Font("Callibri", Font.PLAIN, 16));
        
     
        JTextField jt = new JTextField();
        jt.setBackground(Color.YELLOW);
        jt.setForeground(Color.BLACK);
        tblParamsValues.getColumnModel().getColumn(2).setCellEditor(new javax.swing.DefaultCellEditor(jt));
        
        modelParamsPnlSecond.setRowCount(0);
       
        JScrollPane scrollPanePnlSecond = new JScrollPane(tblParamsValues);
        scrollPanePnlSecond.setBounds(198, 65, 762, 169);
        pnlChild2.add(scrollPanePnlSecond);
        
        modelAssert = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;
			boolean[] canEdit = new boolean[]{
	                    false,false,true
	            };

	           public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit[columnIndex];
	            }
       };
       modelAssert.addColumn("Assertions");
       modelAssert.addColumn("Value");
       modelAssert.addColumn("Delete");
       JTable tblAssert = new JTable(modelAssert);
	
       TableCellRenderer renderPnlSecond1 = new EvenOddRenderer();
       tblAssert.setDefaultRenderer(Object.class, renderPnlSecond1);
  
       TableColumnModel colModel2= tblAssert.getColumnModel();
       colModel2.getColumn(0).setPreferredWidth(250);
       colModel2.getColumn(1).setPreferredWidth(380);
       colModel2.getColumn(2).setPreferredWidth(100);
       ButtonColumn bCol = new ButtonColumn(tblAssert, 2,"tblAssert");
       
       tblAssert.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
       tblAssert.setRowHeight(30);
       tblAssert.setBackground(Color.WHITE);
       tblAssert.setFont(new Font("Callibri", Font.PLAIN, 16));
  
       JScrollPane scrollPaneAsserts = new JScrollPane(tblAssert);
       scrollPaneAsserts.setBounds(198, 335, 762, 169);
       pnlChild2.add(scrollPaneAsserts);
                      
        JButton btnAddAssertions = new JButton("Add Assertions");
        btnAddAssertions.setBackground(new Color(0, 51, 153));
        btnAddAssertions.setForeground(Color.WHITE);
        btnAddAssertions.setFont(new Font("Calibri", Font.BOLD, 20));
        btnAddAssertions.setOpaque(true);
        
        btnAddAssertions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					
				JDialog d = new JDialog(frmTestGeneration, "Assertions"); 
        		
        		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        	    d.setModal(true);
        	    d.setSize(1000, 400); 
                d.setLocationRelativeTo(null);
                JPanel p1=new JPanel();
                p1.setBounds(0, 0, 980, 380);
                String title1= "Select Assertions";
                Border border1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title1,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
        		p1.setBorder(border1);
        		p1.setLayout(null);
				
        		
        		Object[] columnNames = {"Select","Assert Statement","Definition", "Expected Value"};
                Object[][] data = {
                    {false,"assertEquals", "void assertEquals(boolean expected, boolean actual)"},
                    {false,"assertTrue", "void assertTrue(boolean condition)"},
                    {false,"assertFalse", "void assertFalse(boolean condition)"},
                    {false,"assertNotNull", "void assertNotNull(Object object)"},
                    {false,"assertNull", "void assertNull(Object object)"}
                   
                };

                int CHECK_COL=0;
                
                class DataModel extends DefaultTableModel {

                    public DataModel(Object[][] data, Object[] columnNames) {
                        super(data, columnNames);
                    }

                    @Override
                    public void setValueAt(Object aValue, int row, int col) {
                        if (col == CHECK_COL) {
                            for (int r = 0; r < getRowCount(); r++) {
                                super.setValueAt(false, r, CHECK_COL);
                            }
                        }
                        super.setValueAt(aValue, row, col);
                       
                    }
                    private static final long serialVersionUID = 1L;
    				boolean[] canEdit = new boolean[]{
    		                    true, false, false,true,true
    		            };

    		        
                    private boolean any() {
                        boolean result = false;
                        for (int r = 0; r < getRowCount(); r++) {
                            Boolean b = (Boolean) getValueAt(r, CHECK_COL);
                            result |= b;
                        }
                        return result;
                    }

                    @Override
                    public Class<?> getColumnClass(int col) {
                        if (col == CHECK_COL) {
                            return getValueAt(0, CHECK_COL).getClass();
                        }
                        return super.getColumnClass(col);
                    }

                    @Override
                    public boolean isCellEditable(int row, int col) {
                    	return canEdit[col];
                        //return col == CHECK_COL;
                    }
                }
                DataModel model = new DataModel(data, columnNames);
               
                 tbl = new JTable(model) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Class getColumnClass(int column) {
                        switch (column) {
                            case 0:
                                return Boolean.class;
                            case 1:
                                return String.class;
                            case 2:
                                return String.class;
                            
                            default:
                                return String.class;
                        }
                    }
                };
                
                TableColumnModel colModel= tbl.getColumnModel();
                colModel.getColumn(0).setPreferredWidth(80);
                colModel.getColumn(1).setPreferredWidth(167);
                colModel.getColumn(2).setPreferredWidth(390);
                colModel.getColumn(3).setPreferredWidth(270);
                
                tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tbl.setRowHeight(30);
                tbl.setBackground(Color.WHITE);
                tbl.setFont(new Font("Callibri", Font.PLAIN, 16));
               
                tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane sc = new JScrollPane(tbl);
                sc.setBounds(20, 20, 940, 280);
                p1.add(sc);
                getAssertStatementsValue();
                                
                JButton b1,b2;
                b1=new JButton();
                b1.setText("Cancel");
                b1.setOpaque(true);
                b1.setBounds(640, 310, 150, 30);
                b1.setBackground(new Color(0, 51, 153));
                b1.setForeground(Color.WHITE);
                b1.setFont(new Font("Calibri", Font.BOLD, 20));
                p1.add(b1);
                b1.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				d.dispose();
        			}
                });
                                
                b2=new JButton();
                b2.setText("Done");
                b2.setOpaque(true);
                b2.setBounds(810, 310, 150, 30);
                b2.setBackground(new Color(0, 51, 153));
                b2.setForeground(Color.WHITE);
                b2.setFont(new Font("Calibri", Font.BOLD, 20));
                p1.add(b2);
                                
                b2.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				TableModel assertmodel= tbl.getModel();
        				ArrayList<Object> a=null;
        				if (modelAssert.getRowCount()>0)
        				{
        					JOptionPane.showMessageDialog(frmTestGeneration, "Only one Assert Statement Allowed. Delete the existing one.");	
        				}
        				else{
        			
        				for(int i=0;i<assertmodel.getRowCount();i++)
        				{
        					boolean b=((Boolean)assertmodel.getValueAt(i, 0)).booleanValue();
        					if (b)
        					{
        						if(assertmodel.getValueAt(i, 3)== null)
        						{
        							JOptionPane.showMessageDialog(frmTestGeneration, "Please Enter value");
        						}
        						else if(assertmodel.getValueAt(i, 3).toString().length()==0)
        						{
        							JOptionPane.showMessageDialog(frmTestGeneration, "Please Enter value");	
        						}
        						else
        						{
        							boolean found=false;
        							for(int row = 0;row < modelAssert.getRowCount();row++) {
        								for(int col = 0;col < modelAssert.getColumnCount();col++) {
        								   if(modelAssert.getValueAt(row, col).toString().equals(assertmodel.getValueAt(i, 1).toString()))
        									{
        									   found=true;
        									}
        								}
        							}
        							if(found)
        							{
        								JOptionPane.showMessageDialog(frmTestGeneration, "Statement already added!");	
        							}
        							else
        							{
        								modelAssert.addRow(new Object[]{assertmodel.getValueAt(i, 1).toString().trim(),assertmodel.getValueAt(i, 3).toString().trim(),});
        								d.dispose();
        							}
        							
        							
        						}
        						
        					}
        				}
        				}
        			}});
                
                d.getContentPane().add(p1);
                if (modelAssert.getRowCount()>0)
				{
					JOptionPane.showMessageDialog(frmTestGeneration, "Only one Assert Statement Allowed. Delete the existing one.");
					d.setVisible(false);
				}
                else{
                	d.setVisible(true);	
                }
                 
			}
			
				catch(Exception ex)
                {
                	ex.printStackTrace();
                }
			}
			
        });
        
        btnAddAssertions.setFont(new Font("Calibri", Font.PLAIN, 18));
        btnAddAssertions.setBounds(23, 255, 167, 31);
        pnlChild2.add(btnAddAssertions);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setOpaque(true);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Calibri", Font.BOLD, 20));
        btnCancel.setBackground(new Color(0, 51, 153));
        btnCancel.setBounds(728, 549, 102, 31);
        pnlChild2.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		card.next(pnlMethodConfig);
        		btnGenerateTestFile.setEnabled(true);
        		tblMethodList.setEnabled(true);
        	}
        });
        
        JButton btnDone = new JButton("Done");
        btnDone.setOpaque(true);
        btnDone.setForeground(Color.WHITE);
        btnDone.setFont(new Font("Calibri", Font.BOLD, 20));
        btnDone.setBackground(new Color(0, 51, 153));
        btnDone.setBounds(858, 546, 102, 31);
        pnlChild2.add(btnDone);
        
        btnDone.addActionListener(new ActionListener() {
        	@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent arg0) {
        		//********************************* adding test ********************************************
        		if(txtTestName.getText().length()==0)
        		{
        			JOptionPane.showMessageDialog(frmTestGeneration, "Please Enter Test Name");
        		}
        		else{
        			
        		if(txtTestName.isEnabled())
        		{
        			
        			String name=txtTestName.getText();
        			if (testHashMap.containsKey(name))
        			{
        				JOptionPane.showMessageDialog(frmTestGeneration, "Duplicate Test Name!");
        			}
        			else{
        			
        			MultiMap mm=new MultiHashMap();
        			ArrayList<Object> r,ao=null;
        			int nRow = modelParamsPnlSecond.getRowCount(), nCol = modelParamsPnlSecond.getColumnCount();
        			Object[][] tableData = new Object[nRow][nCol];
        			for (int i = 0 ; i < nRow ; i++)
        			{
        				
        				r=new ArrayList<Object>();
        				r.add(modelParamsPnlSecond.getValueAt(i,1));
        				r.add(modelParamsPnlSecond.getValueAt(i,2));
        				
        				mm.put(modelParamsPnlSecond.getValueAt(i,0).toString(), r);
        	      
        			}
        			
        			testParamsHM.put(name, mm);
        			testHashMap.put(methodSelectedID, name);
        			modelExe.addRow(new Object[]{name});
        			selectedTestName=name;
        			
        			//############### Assertions ################
        			if(modelAssert.getRowCount()>0){
        			ao=new ArrayList<Object>();
        			ao.add(modelAssert.getValueAt(0,0));
    				ao.add(modelAssert.getValueAt(0,1));
        			assertStatementsHM.put(selectedTestName, ao);
        			}
        		  }
        			
        		}
        		//*************************************** Editing ***********************************************
        		else
        		{
        			
        			MultiMap map=testParamsHM.get(selectedTestName);
        			Set keySet = map.keySet( );
        		    Iterator keyIterator = keySet.iterator();
        		    String paramName, paramValue;
        		    while( keyIterator.hasNext( ) ) {
        		        Object key = keyIterator.next( );
        		        for(int row=0;row<modelParamsPnlSecond.getRowCount();row++)
            			{
        		        	if(key.equals(modelParamsPnlSecond.getValueAt(row,0)))
        		        	{
        		        		 Collection values = (Collection) map.get( key );
        	        		        Iterator valuesIterator = values.iterator( );
        	        		        while( valuesIterator.hasNext( ) ) {
        	        		        	Object val=valuesIterator.next();
        	        		        	ArrayList<Object> arrlst=(ArrayList<Object>) val;
        	        		        	arrlst.set(0, modelParamsPnlSecond.getValueAt(row,1));
        	        		        	arrlst.set(1, modelParamsPnlSecond.getValueAt(row,2));
        		        	}
            			}
        		                		        	
        		       }
        		    }
        			        			
        		    //filling assertion table
        		    if(assertStatementsHM.containsKey(selectedTestName)){
        		    	ArrayList<Object> a=assertStatementsHM.get(selectedTestName);
        		    	modelAssert.addRow(new Object[]{a.get(0).toString(), a.get(1).toString()});
        		    
        		    }
        		    else
        		    {
        		    	if (modelAssert.getRowCount()>0){
        		    	ArrayList<Object> a=new ArrayList<Object>();
        		    	a.add(modelAssert.getValueAt(0,0));
        		    	a.add(modelAssert.getValueAt(0,1));
        		    	assertStatementsHM.put(selectedTestName, a);
        		    	}
        		    }
        		}
        		card.next(pnlMethodConfig);
        		btnGenerateTestFile.setEnabled(true);
        		tblMethodList.setEnabled(true);
        		tblMethodList.setCellSelectionEnabled(true);
        	}
        		
        	}
        });
        
        
        JLabel lblListOfAssert = new JLabel("List Of Assert Statements:");
        lblListOfAssert.setFont(new Font("Calibri", Font.BOLD, 18));
        lblListOfAssert.setBounds(23, 299, 216, 23);
        pnlChild2.add(lblListOfAssert);
        
		        
		btnGenerateTestFile = new JButton("Generate Test File");
		btnGenerateTestFile.setForeground(Color.WHITE);
		btnGenerateTestFile.setOpaque(true);
		btnGenerateTestFile.setEnabled(false);
		btnGenerateTestFile.setFont(new Font("Calibri", Font.BOLD, 20));
		btnGenerateTestFile.setBackground(new Color(0, 51, 153));
		btnGenerateTestFile.setBounds(1651, 927, 198, 34);
		btnGenerateTestFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ************************************************* GENERATE TEST FILE *************************************
				String fileContent=generateTestCasesNew();
				
				JDialog d = new JDialog(frmTestGeneration, "File Content"); 
        		
        		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        	    d.setModal(true);
        	    d.setSize(920, 600); 
                d.setLocationRelativeTo(null);
                JPanel p1=new JPanel();
                p1.setBounds(0, 0, 910, 580);
                String title1= "JUnit Test Content";
                Border border1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title1,TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION, new Font("Callibri", Font.PLAIN, 16), Color.decode("#003399"));
        		p1.setBorder(border1);
        		p1.setLayout(null);
        		
        		JTextArea tx = new JTextArea();
        		Font font = new Font("Calibri", Font.PLAIN, 18);
                tx.setFont(font);
        		tx.setEditable(false);
        		JScrollPane sp = new JScrollPane(tx);
                sp.setBounds(20, 20, 900, 480);
                p1.add(sp);
                
                tx.setText(fileContent);
                
                JButton b1;
                b1=new JButton();
                b1.setText("SAVE AS FILE");
                b1.setOpaque(true);
                b1.setBounds(660, 510, 150, 30);
                b1.setBackground(new Color(0, 51, 153));
                b1.setForeground(Color.WHITE);
                b1.setFont(new Font("Calibri", Font.BOLD, 20));
                b1.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				
        				String fileName= cls.getSimpleName() +  "Test";
        				JFileChooser fileChooser = new JFileChooser();
        				fileChooser.setSelectedFile(new File(fileName));
        				        				
        				FileFilter filter = new FileNameExtensionFilter(".java", "java", "java");
        				fileChooser.addChoosableFileFilter(filter);
        				fileChooser.setAcceptAllFileFilterUsed(false);
        				int retrival = fileChooser.showSaveDialog(null);
        				if (retrival == JFileChooser.APPROVE_OPTION) {
        					FileWriter fw=null ;
        			        try {
        			            fw= new FileWriter(fileChooser.getSelectedFile()+".java");
        			            fw.write(fileContent);
        			            fw.close();
        			            JOptionPane.showMessageDialog(frmTestGeneration, "File saved successfully!");
        			        } catch (Exception ex) {
        			        	 ex.printStackTrace();
        			        }
        			        
        			    }
        				
        			}
                });
                
                p1.add(b1);
                d.getContentPane().add(p1);
                d.setVisible(true);
				
			}
		});
		frmTestGeneration.getContentPane().add(btnGenerateTestFile);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(0, 51, 153));
		panel_3.setBounds(0, 994, 1920, 10);
		frmTestGeneration.getContentPane().add(panel_3);
		
		JLabel lblEnterPackage = new JLabel("Enter package name :");
		lblEnterPackage.setForeground(new Color(0, 51, 153));
		lblEnterPackage.setFont(new Font("Calibri", Font.BOLD, 25));
		lblEnterPackage.setBounds(210, 207, 229, 34);
		frmTestGeneration.getContentPane().add(lblEnterPackage);
		
	
	}

	//delete prev file
	boolean deleteDirectory(File directoryToBeDeleted) {
		
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	//for tblAssert Value column
	public void getAssertStatementsValue()
	{ 
		String methodName=hmMethod.get(methodSelectedID).getName().toString();
		String value="";
		if (modelParamsPnlSecond.getRowCount()>0){
			for(int i=0;i<modelParamsPnlSecond.getRowCount();i++)
			{
				value+=modelParamsPnlSecond.getValueAt(i, 0).toString() + ",";
			}
			value=value.substring(0, value.length()-1);
		}
		if(tblMethodList.getModel().getValueAt(methodSelectedID, 1).toString().contains("static"))
		{
			value=txtPackageName.getText()+ "." + cls.getSimpleName() + "."+ methodName+ "(" + value+ ")";
		}
		else
		{
			value="obj."+ methodName+ "(" + value+ ")";
		}
		tbl.getModel().setValueAt(value, 1, 3);
		tbl.getModel().setValueAt(value, 2, 3);
		tbl.getModel().setValueAt(value, 3, 3);
		tbl.getModel().setValueAt(value, 4, 3);
		
		String returntype=hmMethod.get(methodSelectedID).getReturnType().toString();
		switch (returntype)
		{
			case "int":
				tbl.getModel().setValueAt(0, 0, 3);
			case "float":
				tbl.getModel().setValueAt(0.0, 0, 3);
			case "double":
				tbl.getModel().setValueAt(0.0, 0, 3);
			case "long":
				tbl.getModel().setValueAt(0, 0, 3);
			case "short":
				tbl.getModel().setValueAt(0, 0, 3);
			case "byte":
				tbl.getModel().setValueAt(0, 0, 3);
			case "boolean":
				tbl.getModel().setValueAt(0, 0, 3);
			default :
				tbl.getModel().setValueAt("null", 0, 3);
		}
				
	}
	
	public void getMethodAndParametersInfo()
	{
		Method[] methods = cls.getDeclaredMethods();
		hmMethod.clear();
		
		ArrayList<Parameter> paramList=null;
		paramsHM.clear();
		Parameter[] params;
		
		for(int count=0; count<methods.length;count++)
		{			
			hmMethod.put(count,methods[count]);
			
			params= methods[count].getParameters();
			paramList= new ArrayList<Parameter>();
			 for (int i=0; i<params.length;i++)
			 {
				 paramList.add(params[i]);
			 }
			 paramsHM.put(count, paramList);
		}
		
	}
	
	public static String getModifierName(int value,int methodID)
	 {
		String returnModifier="";
		if((value & Modifier.STATIC) != 0)
		{ 
			staticMethodHM.put(methodID, "static");
			returnModifier= "static "; 
		}   
		//check for static
		if (Modifier.PUBLIC==value)
		 {
			returnModifier+= "Public";
		 }
		 else if (Modifier.PRIVATE==value ){ returnModifier+= "Private";}
		 else {returnModifier+= "Protected";}
		
		return returnModifier;
	 }
	
	public  Class<?> loadClassAndGetClassName(String sourceFilePath,String packageName)
	{
		try
		{
			String old=sourceFilePath;
			String last = old.substring(old.lastIndexOf('\\') + 1);
			String new1= last.substring(0,last.lastIndexOf("."));
			
			File source = new File(old);
			String clsName=packageName + "." + new1;
			strclassName=clsName;
			packageName=packageName.replace(".", "\\\\");
			//temporary file to load .class file
			File destDir=new File( "D:\\" + packageName) ;
			FileUtils.copyFileToDirectory(source, destDir);
			File file = new File("D:/"); 
			prevFilePath=destDir;
	        //convert the file to URL format
			URL url = file.toURI().toURL(); 
			URL[] urls = new URL[]{url}; 
				
	        //load this folder into Class loader
			ClassLoader cl = new URLClassLoader(urls); 
			Class  cls1 = cl.loadClass(clsName);		
			return cls1;
		}
		catch(NoClassDefFoundError ex)
		{
			JOptionPane.showMessageDialog(frmTestGeneration, "Please check package name");
			return null;
		}
		catch(ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(frmTestGeneration, "Please check package name");
			return null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
		}
		return null;
		
	}
	
	public static String getMethodOfTest(Object testName)
	{	
		List list;
		for (Object o : testHashMap.keySet()) {
			
			list = (List)testHashMap.get(o);
			if (list.size()>0)
			{
				for (int p=0;p<list.size();p++)
				{
					if (list.get(p).toString().equals(testName))
					{
						for (Object o1 : hmMethod.keySet()) {
							if (o1.toString().equals(o.toString()))
								return hmMethod.get(o1).getName() + "," + o1.toString();
					 	}
					}
				}
				
			}
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public  String generateTestCasesNew()
	{
		String last = cls.getSimpleName(); 
		String mainHeadline= "package " + txtPackageName.getText() + ";\n";
		mainHeadline= mainHeadline+ "import org.junit.Test;\n";
		if (assertStatementsHM.size()>0)
		{
			 mainHeadline= mainHeadline + "import static org.junit.Assert.*;\n";
		}
		mainHeadline=mainHeadline + "\npublic class " + last + "Test" + " {\n\n";
		
		String instance= "\t\t"+ txtPackageName.getText()+ "." + cls.getSimpleName() + " obj =  new " + txtPackageName.getText()+ "." + cls.getSimpleName() + "();\n";
		
		String testHeadline="", paramHeadline="",strCallMethod="",assertHeadline="";
		
		Set<String> pkeySet = testParamsHM.keySet( );
		Iterator pkeyIterator = pkeySet.stream().iterator();
		ArrayList<String> arl=new ArrayList<String>();
		ArrayList<String> arl2=new ArrayList<String>();
		String mName="";
		TreeMap<Integer,String> lhm=new TreeMap<Integer, String>();
		TreeMap<Integer,String> lhm1=new TreeMap<Integer, String>();
		
	    while( pkeyIterator.hasNext( ) ) {
	    	mName="";
	        Object key = pkeyIterator.next( );
	        testHeadline=testHeadline+"\t@Test\n\tpublic void " + key + "() throws Exception {\n" ;
	        paramHeadline="";assertHeadline="";arl.clear();arl2.clear();
	        lhm.clear();lhm1.clear();
	        MultiMap map=testParamsHM.get(key);
	        //original code
	        Set keySet = map.keySet( );
		    Iterator keyIterator = keySet.iterator();
		    String paramName,strParamInMethod="";String m="",arrayString="";
		    int id=0;
		    
		    mName=getMethodOfTest(key).toString().split(",")[0];
		    
		    id=Integer.parseInt(getMethodOfTest(key).toString().split(",")[1]);
		    int pID=0;
		    Parameter pp=null;
		    while( keyIterator.hasNext( ) ) {
		        Object key1 = keyIterator.next( );
		        paramName=key1.toString();
		        
		        ArrayList<Parameter> ap=paramsHM.get(id);
		        for(int l=0;l<ap.size();l++)
		        {
		        	if(ap.get(l).getName().equals(paramName))
		        	{
		        		pID=l;
		        		lhm.put(l, paramName);
		        	}
		        }
		       
		        Collection values = (Collection) map.get( key1 );
		        Iterator valuesIterator = values.iterator( );
		        while( valuesIterator.hasNext( ) ) {
		        	Object val=valuesIterator.next();
		        	ArrayList<Object> arrlst=(ArrayList<Object>) val;
		        	
		        	if(arrlst.get(0).toString().contains("Array")){
		        		if (arrlst.get(1).toString().contains("|") )
		        		{
		        		String arra[]= arrlst.get(1).toString().split("\\|");
		        		arrayString="";
		        		if (arra.length>0)
		        		{
		        			if(arrlst.get(0).toString().equals("String Array")){
		        				for(int k=0;k<arra.length;k++)
		        				{
		        					arrayString=arrayString+"\"" + arra[k]+"\",";
		        				}
		        				arrayString=arrayString.substring(0, arrayString.length()-1);
		        				m="\t\tString[] " + paramName+"={"+arrayString + "};\n";
		        			}
		        			else{
		        				for(int k=0;k<arra.length;k++)
		        				{
		        					arrayString=arrayString+ arra[k]+",";
		        				}
		        				arrayString=arrayString.substring(0, arrayString.length()-1);
		        				m="\t\t" + arrlst.get(0).toString().substring(0,arrlst.get(0).toString().lastIndexOf(" ")) + "[]  " + paramName+"={"+arrayString + "};\n";
		        			}
		        		}
		        		}
		        		else
		        		{
		        			m="\t\t" + arrlst.get(0).toString().substring(0,arrlst.get(0).toString().lastIndexOf(" ")) + "[]  " + paramName+"= null;\n";
		        		}
		        		
		        		
		        	}
		        	else{
		        		if(arrlst.get(1).toString().length()==0)
		        		{
		        			m="\t\t" + arrlst.get(0).toString() + " " + paramName + " = null;\n";
		        		}
		        		else{m="\t\t" + arrlst.get(0).toString() + " " + paramName + " = " +  arrlst.get(1).toString() + ";\n";}
		        	}
		        	lhm1.put(pID, m);
		        }
		       
		       
	         }// end of paramater loop
		    
		    for(Map.Entry ma:lhm.entrySet()){    
		        strParamInMethod=strParamInMethod + ma.getValue() + ",";
		        paramHeadline=paramHeadline+ lhm1.get(ma.getKey());
		       }
		    
		    if (strParamInMethod.length()>0)
		    {
		    	strParamInMethod=strParamInMethod.substring(0, strParamInMethod.length() - 1);
		    }
		   
		    //for static method
		    if(staticMethodHM.containsKey(id)){
		    if (staticMethodHM.get(id).toString().equals("static"))
		    {
		    	instance="";
		    	//assertHeadline="";
		    	if(assertStatementsHM.containsKey(key))
			    {
			    	strCallMethod="";
			    	String s=txtPackageName.getText()+ "." + cls.getSimpleName() + "."+ mName + "(" + strParamInMethod + ")";
			    	ArrayList<Object>arr= (ArrayList<Object>) assertStatementsHM.get(key);
			    	if(arr.get(0).toString().equals("assertEquals")){
			    		assertHeadline="\t\t"+ arr.get(0).toString() + "(" + arr.get(1).toString() + "," + s + ");\n" ;
			    	}
			    	else
			    	{
			    		assertHeadline="\t\t"+ arr.get(0).toString() + "(" +arr.get(1).toString() +");\n" ;
			    	}
			    	
			    	 
			    }
		    	else{
		    	strCallMethod="\t\t"+txtPackageName.getText()+ "." + cls.getSimpleName() + "." + mName + "(" + strParamInMethod + ");\n";}
		    }
	       }
		    else{
		    instance= "\t\t"+ txtPackageName.getText()+ "." + cls.getSimpleName() + " obj =  new " + txtPackageName.getText()+ "." + cls.getSimpleName() + "();\n";
		  //get assert statements
		    if(assertStatementsHM.containsKey(key))
		    {
		    	strCallMethod="";
		    	String s="obj."+ mName + "(" + strParamInMethod + ")";
		    	ArrayList<Object>arr= (ArrayList<Object>) assertStatementsHM.get(key);
		    	if(arr.get(0).toString().equals("assertEquals")){
		    		assertHeadline="\t\t"+ arr.get(0).toString() + "(" + arr.get(1).toString() + "," + s + ");\n" ;
		    	}
		    	else{
		    		assertHeadline="\t\t"+ arr.get(0).toString() + "(" + arr.get(1).toString() + ");\n" ;
		    	}
		    }
		    else
		    {
		    	if(mName != null)
			    { strCallMethod="\t\tobj."+ mName + "(" + strParamInMethod + ");\n";}
		    }
		    }
		    testHeadline=testHeadline + paramHeadline +instance+ strCallMethod + assertHeadline + "\n\t}\n";
		   
	    }//end of test loop
	    String wholeFile=mainHeadline + testHeadline +"\n}\n";
	    return wholeFile;
	}
	
	@SuppressWarnings("unchecked")
	public static void makeDefaultTestForAllMethods()
	{
		MultiMap mhm = null;
		ArrayList<Object> arr=null;
		for(int m=0;m< hmMethod.size(); m++)
		{
			Method me =hmMethod.get(m);
			String defaultTestName=me.getName() + "_DefaultTest" ;
			testHashMap.put(m, defaultTestName);
			//set default params
			ArrayList<Parameter> paramList= paramsHM.get(m);
			mhm = new MultiHashMap();
			for(int k=0 ; k<paramList.size();k++)
			{
				Parameter p= paramList.get(k);
				Class<?> paramClass= p.getClass();
				arr=new ArrayList<Object>();
				String paramType=p.getParameterizedType().getTypeName();
				    
				if(p.getParameterizedType().getTypeName().equals("java.lang.String[]"))
				{
					arr.add("String Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("int[]"))
				{
					arr.add("int Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("float[]"))
				{
					arr.add("float Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("long[]"))
				{
					arr.add("long Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("double[]"))
				{
					arr.add("double Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("short[]"))
				{
					arr.add("short Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("byte[]"))
				{
					arr.add("byte Array");
					arr.add("null");
				}
				else if(p.getParameterizedType().getTypeName().equals("boolean[]"))
				{
					arr.add("boolean Array");
					arr.add("null");
				}
				else if (p.getParameterizedType().getTypeName().contains("int"))
				{
					arr.add("int");
					arr.add(0);
				
				}
				else if( p.getParameterizedType().getTypeName().contains("long") )
				{
					arr.add("long");
					arr.add(0);
					
				}
				else if( p.getParameterizedType().getTypeName().contains("float"))
				{
					arr.add("float");
					arr.add(0.0);
					
				}
				else if(p.getParameterizedType().getTypeName().contains("double") )
				{
					arr.add("double");
					arr.add(0.0);
					
				}
				else if(p.getParameterizedType().getTypeName().contains("short") )
				{
					arr.add("short");
					arr.add(0);
					
				}
				else if(p.getParameterizedType().getTypeName().contains("byte"))
				{
					arr.add("byte");
					arr.add(0);
					
				}
				else if(p.getParameterizedType().getTypeName().contains("String") )
				{
					arr.add("String");
					arr.add("");
					
				}
				
				else if(p.getParameterizedType().getTypeName().contains("boolean") )
				{
					arr.add("boolean");
					arr.add(false);
					
				}
				mhm.put(p.getName(),arr);
				
			}
			
			//add to testParamsHM 
			testParamsHM.put(defaultTestName, mhm);
			
		}
		
	}
	
	
	
	public static String getClassName(InputStream is) throws Exception {
	    DataInputStream dis = new DataInputStream(is);
	    dis.readLong(); 
	    int cpcnt = (dis.readShort()&0xffff)-1;
	    int[] classes = new int[cpcnt];
	    String[] strings = new String[cpcnt];
	    for(int i=0; i<cpcnt; i++) {
	        int t = dis.read();
	        if(t==7) classes[i] = dis.readShort()&0xffff;
	        else if(t==1) strings[i] = dis.readUTF();
	        else if(t==5 || t==6) { dis.readLong(); i++; }
	        else if(t==8) dis.readShort();
	        else dis.readInt();
	    }
	    dis.readShort(); 
	    return strings[classes[(dis.readShort()&0xffff)-1]-1].replace('/', '.');
	}
	
	
class ButtonColumn extends AbstractCellEditor
implements TableCellRenderer, TableCellEditor, ActionListener
{
JTable table;
JButton renderButton,renderButton1,renderAssertBtn;
JButton editButton,deleteButton,deleteAssertBtn;
String text;
String tName;
public ButtonColumn(JTable table, int column,String strtblName)
{
    super();
    this.table = table;
    this.tName=strtblName;
    if(strtblName.equals("tblAssert"))
    	{
    	renderAssertBtn = new JButton();

    	deleteAssertBtn = new JButton();
    	deleteAssertBtn.setFocusPainted( false );
    	deleteAssertBtn.addActionListener( this );
    	}
   
    else{
    if (column==1)
    {
    renderButton = new JButton();

    editButton = new JButton();
    editButton.setFocusPainted( false );
    editButton.addActionListener( this );
    }
    else if(column==2){
    	renderButton1 = new JButton();

        deleteButton = new JButton();
        deleteButton.setFocusPainted( false );
        deleteButton.addActionListener( this );
        }
    
    }
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(column).setCellRenderer( this );
    columnModel.getColumn(column).setCellEditor( this );
}
public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{
	 if(tName.equals("tblAssert"))
	 {
		 if (hasFocus)
		    {
			 renderAssertBtn.setForeground(table.getForeground());
			 renderAssertBtn.setBackground(UIManager.getColor("Button.background"));
		        
		    }
		    else if (isSelected)
		    {
		    	renderAssertBtn.setForeground(table.getSelectionForeground());
		    	renderAssertBtn.setBackground(table.getSelectionBackground());
		       
		    }
		    else
		    {
		    	renderAssertBtn.setForeground(table.getForeground());
		    	renderAssertBtn.setBackground(UIManager.getColor("Button.background"));
		        
		    }
		renderAssertBtn.setText("Delete");
    	return renderAssertBtn;
	}
	 else{
	if (column==1)
    {
    if (hasFocus)
    {
        renderButton.setForeground(table.getForeground());
        renderButton.setBackground(UIManager.getColor("Button.background"));
        
    }
    else if (isSelected)
    {
        renderButton.setForeground(table.getSelectionForeground());
        renderButton.setBackground(table.getSelectionBackground());
       
    }
    else
    {
        renderButton.setForeground(table.getForeground());
        renderButton.setBackground(UIManager.getColor("Button.background"));
        
    }}
    else if (column==2)
    {
    	if (hasFocus)
        {
           
            renderButton1.setForeground(table.getForeground());
            renderButton1.setBackground(UIManager.getColor("Button.background"));
        }
        else if (isSelected)
        {
          
            renderButton1.setForeground(table.getSelectionForeground());
            renderButton1.setBackground(table.getSelectionBackground());
        }
        else
        {
          
            renderButton1.setForeground(table.getForeground());
            renderButton1.setBackground(UIManager.getColor("Button.background"));
        }
    }
 
  
    if (column==1)
    {
    	renderButton.setText("Edit");
    	return renderButton;
    }else if(column==2)
    {
    renderButton1.setText("Delete");
    return renderButton1;
    }
	 }
    return null;
}

public Component getTableCellEditorComponent(
    JTable table, Object value, boolean isSelected, int row, int column)
{
    text = (value == null) ? "" : value.toString();
    if(tName.equals("tblAssert"))
	 {
    	deleteAssertBtn.setText( text );
		return deleteAssertBtn;
	 }
    else{
    	if (column==1)
    	{
    		editButton.setText( text );
    		return editButton;
    	}
    	else if (column==2){
    		deleteButton.setText(text);
    		return deleteButton;
    	}
    }
    return null;
}

public Object getCellEditorValue()
{
    return text;
}

public void actionPerformed(ActionEvent e)
{
	
    fireEditingStopped();
    Object src = e.getSource();
    if (src==deleteAssertBtn)
    {
    	//delete Assert from table and assert list
    	int r = table.getSelectedRow();
    	if(r >= 0){
       	 
       	 	String assertName=modelAssert.getValueAt(r, 0).toString();
            // remove a row from jtable
       	 	int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Assert Statement: " +assertName + "?",  "Delete Confirmation",
       				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
       	 	if (input==0)
       	 	{
       	 		//delete from testHashMap and TestParamsHM
       	 		if( txtTestName.getText().trim().equals(selectedTestName))
       	 		{
       	 			if (assertStatementsHM.containsKey(selectedTestName))
       	 			{
       	 				assertStatementsHM.remove(selectedTestName);
       				
       	 			}
       	 		}
  			
   			 modelAssert.removeRow(r);
   			 }
       	 }
   	 
    	
    }
    
    else{  
    
    if (src==deleteButton)
    { 
    	 int i = table.getSelectedRow();
    	  
         if(i >= 0){
        	 
        	 String testname=modelExe.getValueAt(i, 0).toString();
             // remove a row from jtable
        	 int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete test: " +testname + "?",  "Delete Confirmation",
        				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        	 if (input==0)
        	 {
        		 
        		 //delete from testHashMap and TestParamsHM
        		 if (testHashMap.containsKey(methodSelectedID))
        		 {
        			 boolean bool=false;
        			 List lst=(List)testHashMap.get(methodSelectedID);
        			 Iterator<String> it = lst.iterator();
        			 while (it.hasNext()) {
        				 if (it.next().toString().equals(testname)) {
        					 bool=true;
         			    	}
        			 }
        			 if(bool==true)
        			 {
        			
                		 if(testParamsHM.containsKey(testname))
                		 {
                			 testParamsHM.remove(testname);
                			 
                		 }
        				 List lstFound=(List)testHashMap.get(methodSelectedID);
        				 testHashMap.remove(methodSelectedID,  lstFound);
   			    	     modelExe.removeRow(i);
   			    	     JOptionPane.showMessageDialog(frmTestGeneration, "Test Deleted Successfully!");
        			 }
        			 bool=false;
            		
        		 }
        		
        		 
        		 
        	 }
         }
        
    }
    else if (src== editButton)
    {
    
   
    card.next(pnlMethodConfig);
    txtTestName.setEnabled(false);
	int i = table.getSelectedRow();
    selectedTestName=table.getValueAt(i,  0).toString();
    txtTestName.setText(selectedTestName);
    if (selectedTestName !=null){
    	modelParamsPnlSecond.setRowCount(0);
        MultiMap map=testParamsHM.get(selectedTestName);
        Set keySet = map.keySet( );
	    Iterator keyIterator = keySet.iterator();
	    String paramName, paramValue;
	    while( keyIterator.hasNext( ) ) {
	        Object key = keyIterator.next( );
	        paramName=key.toString();
	        Collection values = (Collection) map.get( key );
	        Iterator valuesIterator = values.iterator( );
	        while( valuesIterator.hasNext( ) ) {
	        	Object val=valuesIterator.next();
	        	ArrayList<Object> arrlst=(ArrayList<Object>) val;
	            if(arrlst.get(1).toString().length()==0)
	        	{
	        	String paramType="",paramDefaultVal="";
	   	    	if(arrlst.get(i).toString().equals("String Array"))
				{
	   	    		paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("int Array"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("float Array"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("long Array"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("double Array"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("short[]"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("byte Array"))
				{
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().equals("boolean Array"))
				{
					paramDefaultVal="null";
				}
				else if (arrlst.get(i).toString().toString().contains("int"))
				{
					paramDefaultVal="0";
					
				}
				else if( arrlst.get(i).toString().contains("long") )
				{
					paramDefaultVal="0";
								
				}
				else if(arrlst.get(i).toString().contains("float"))
				{
					paramDefaultVal="0.0";
					
				}
				else if(arrlst.get(i).toString().contains("double") )
				{
					paramDefaultVal="0.0";
					
				}
				else if(arrlst.get(i).toString().contains("short") )
				{
					paramDefaultVal="0";
					
				}
				else if(arrlst.get(i).toString().contains("byte"))
				{
					
					paramDefaultVal="0";
					
				}
				else if(arrlst.get(i).toString().contains("String") )
				{
					
					paramDefaultVal="null";
					
				}
				else if(arrlst.get(i).toString().contains("java.class.String"))
				{
					
					paramDefaultVal="null";
				}
				else if(arrlst.get(i).toString().contains("boolean") )
				{
					
					paramDefaultVal="false";
					
				}
				modelParamsPnlSecond.addRow(new Object[] { paramName,arrlst.get(0).toString(), paramDefaultVal });
	        	}
	        	else{ modelParamsPnlSecond.addRow(new Object[] {paramName, arrlst.get(0).toString(), arrlst.get(1).toString()});}
	          
	        }
	        System.out.print( "\n" );
	    }
	    //############## Assertions ###############################
	    modelAssert.setRowCount(0);
	    Set<String> keysS = assertStatementsHM.keySet();
	    Iterator<String> keySetIterator = keysS.iterator();
	    while (keySetIterator.hasNext()) {
	    	String key = keySetIterator.next();
	    	if (key.equals(selectedTestName))
	    	{	
	    		ArrayList<Object> ao=(ArrayList<Object>)assertStatementsHM.get(key);
	    		
	    		modelAssert.addRow(new Object[] {ao.get(0).toString(),ao.get(1).toString() });
	    	}
	    }
	    
	    
        }
    btnGenerateTestFile.setEnabled(false);
    tblMethodList.setEnabled(false);
    tblMethodList.setCellSelectionEnabled(false);
    }
}
}
}
}




class EvenOddRenderer implements TableCellRenderer {

	  public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

	  public Component getTableCellRendererComponent(JTable table, Object value,
	      boolean isSelected, boolean hasFocus, int row, int column) {
	    Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(
	        table, value, isSelected, hasFocus, row, column);
	    ((JLabel) renderer).setOpaque(true);
	    Color foreground, background;
	    if (table.isEnabled() == false)
	    {
	    	 foreground = Color.BLACK;
		     background = Color.LIGHT_GRAY;
	    }else{
	    if (isSelected) {
	      foreground = Color.BLACK;
	      background = Color.LIGHT_GRAY;
	    }
	    else {
	      if (row % 2 == 0) {
	        foreground = Color.decode("#003399");
	        background = Color.white;
	      } else {
	    	  foreground = Color.black;
		      background = Color.decode("#c8d9f4");
	      
	      }
	    }
	  }
	    renderer.setForeground(foreground);
	    renderer.setBackground(background);
	    return renderer;
	  }
	}



package com.imco.costoptimization.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Servlet implementation class FileUpload
 */
@WebServlet("/fileupload")
@MultipartConfig
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUpload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
//		String yourName = request.getParameter("excelfile");
		response.setContentType("text/html;charset=UTF-8");

	    // Create path components to save the file
	    final String path = "/temp";
	    final Part filePart = request.getPart("excelfile");
	    final String fileName = getFileName(filePart);

	    OutputStream out = null;
	    InputStream filecontent = null;
	    final PrintWriter writer = response.getWriter();

	    try {
	        out = new FileOutputStream(new File(path + File.separator
	                + fileName));
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        
	        //File myFile = new File(path + File.separator + fileName);
	        //FileInputStream fis = new FileInputStream(myFile);

	        // Finds the workbook instance for XLSX file
	        XSSFWorkbook myWorkBook = new XSSFWorkbook (path + File.separator + fileName);

	        // Return first sheet from the XLSX workbook
	        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

	        // Get iterator to all the rows in current sheet
	        Iterator<Row> rowIterator = mySheet.iterator();
	        Map uniqueClient = new HashMap();
	        SummaryTableDataSet summaryTableOneRow=null;
//	        writer.println("<table >");
	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
//	            writer.println("<tr>");
	            // For each row, iterate through each columns
	            Iterator<Cell> cellIterator = row.cellIterator();
	            double payg_1y=0.0;
            	double ri_1y_PaygPrice=0.0;
            	int cellcount = 1;
	            while (cellIterator.hasNext()) {
//	            	writer.println("<td>");
	            	
	            	Cell cell = cellIterator.next();
	                if(cellcount==1) {
	                	if (cell.getCellTypeEnum() == CellType.STRING) {
	                		summaryTableOneRow.setCustomerName(cell.getStringCellValue());
	                	}
	                }
	                if(cellcount==2) {
	                	if (cell.getCellTypeEnum() == CellType.STRING) {
	                		summaryTableOneRow.setSubscription(cell.getStringCellValue());
	                	}
	                }
	                if(cellcount==3) {
	                	if (cell.getCellTypeEnum() == CellType.STRING) {
	                		summaryTableOneRow.setMeterName(cell.getStringCellValue());
	                	}
	                }
	                if(cellcount==4) {
	                	if (cell.getCellTypeEnum() == CellType.STRING) {
	                		summaryTableOneRow.setMeterRegion(cell.getStringCellValue());
	                	}
	                }
	                if(cellcount==17) {
	                	if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	                		payg_1y=cell.getNumericCellValue();
	                		summaryTableOneRow.setPayG_1y(payg_1y);
	                	}
	                }
	                if(cellcount==20) {
	                	if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	                		ri_1y_PaygPrice=cell.getNumericCellValue();
	                		summaryTableOneRow.setRi_1Y_PAYGPrice(ri_1y_PaygPrice);
	                	}
	                }
	                summaryTableOneRow.setRi_1Y_Savings_PAYG(payg_1y-ri_1y_PaygPrice);
	                summaryTableOneRow.setBreakEvenMOnths_1YR(0.0);
	                
	                if (cell.getCellTypeEnum() == CellType.STRING) {
	                	writer.print(cell.getStringCellValue());
	                } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	                	writer.print(cell.getNumericCellValue());
	                }
//	                writer.println("</td>");
	            }
	            cellcount++;
	            
//	            writer.println("</tr>"); 
	        }
	        System.out.println(summaryTableOneRow.getCustomerName());
//	        writer.println("</table>");
	        
	        
//	        LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", 
//	                new Object[]{fileName, path});
	    } catch (FileNotFoundException fne) {
	        writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");
	        writer.println("<br/> ERROR: " + fne.getMessage());

	        
//	        LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", 
//	                new Object[]{fne.getMessage()});
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        if (writer != null) {
	            writer.close();
	        }
	    }
	}
	
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

}


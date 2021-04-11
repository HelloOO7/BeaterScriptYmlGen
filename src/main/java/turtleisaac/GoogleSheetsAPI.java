package turtleisaac;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheetsAPI {

	private Sheets sheetsService;
	private String APPLICATION_NAME = "Bs2Yml";
	private String SPREADSHEET_ID;
	private String SPREADSHEET_LINK;
	private String projectPath;

	public GoogleSheetsAPI(String spreadsheetLink, String projectPath) throws IOException, GeneralSecurityException {
		if (!spreadsheetLink.contains("https://")) {
			spreadsheetLink = "https://" + spreadsheetLink;
		}

		SPREADSHEET_LINK = spreadsheetLink;
		this.projectPath = projectPath;

		SPREADSHEET_ID = spreadsheetLink.split("/")[5];
		sheetsService = getSheetsService();
	}

	private Credential authorize() throws IOException, GeneralSecurityException {
		InputStream in = GoogleSheetsAPI.class.getResourceAsStream("/credentials.json");

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

		List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
				.setDataStoreFactory(new FileDataStoreFactory(new File(projectPath + "/tokens")))
				.setAccessType("offline")
				.build();

		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

		return credential;
	}

	public Sheets getSheetsService() throws IOException, GeneralSecurityException {
		Credential credential = authorize();
		return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public List<List<Object>> getResponse(String subSheet, String range) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, subSheet + "!" + range).execute();

		List<List<Object>> values = response.getValues();

		return values;
	}

	public List<List<String>> getSpecifiedSheet(String subSheet) throws IOException {
		ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, subSheet).execute();

		List<List<Object>> values = response.getValues();
		List<List<String>> strings = new ArrayList<>();
		for (List<Object> ol : values){
			ArrayList al = new ArrayList();
			for (Object o : ol){
				al.add((String)o);
			}
			strings.add(al);
		}

		return strings;
	}
	
	public List<List<CellFormat>> getSpecifiedSheetCellFormats(String subSheet) throws IOException {
		Spreadsheet ss = sheetsService.spreadsheets().get(SPREADSHEET_ID).setIncludeGridData(true).execute();

		Sheet s = null;
		for (Sheet s2 : ss.getSheets()){
			if (s2.getProperties().getTitle().equals(subSheet)){
				s = s2;
				break;
			}
		}
		
		List<List<CellFormat>> cellFormats = new ArrayList<>();

		if (s != null){
			GridData gd = s.getData().get(0);
			
			for (RowData row : gd.getRowData()){
				List<CellFormat> rowFormats = new ArrayList<>();
				for (CellData cell : row.getValues()){
					CellFormat fmt = cell.getEffectiveFormat();
					rowFormats.add(fmt);
				}
				cellFormats.add(rowFormats);
			}
		}
		
		return cellFormats;
	}
	
	public List<List<Color>> getSpecifiedSheetTextColors(String subSheet) throws IOException {
		Spreadsheet ss = sheetsService.spreadsheets().get(SPREADSHEET_ID).setIncludeGridData(true).execute();

		Sheet s = null;
		for (Sheet s2 : ss.getSheets()){
			if (s2.getProperties().getTitle().equals(subSheet)){
				s = s2;
				break;
			}
		}
		
		List<List<Color>> l = new ArrayList<>();
		if (s != null){
			GridData gd = s.getData().get(0);
			
			for (RowData row : gd.getRowData()){
				List<Color> colors = new ArrayList<>();
				for (CellData cell : row.getValues()){
					CellFormat fmt = cell.getEffectiveFormat();
					colors.add(fmt.getTextFormat().getForegroundColor());
				}
				l.add(colors);
			}
		}
		
		return l;
	}

	public String[][] getSpecifiedSheetArr(String subSheet) throws IOException {
		List<List<String>> values = getSpecifiedSheet(subSheet);

		String[][] ret = new String[values.size()][];

		for (int i = 0; i < values.size(); i++) {
			ret[i] = values.get(i).toArray(new String[0]);
		}

		return ret;
	}

	public String[] getSheetNames() throws IOException {
		Spreadsheet response1 = sheetsService.spreadsheets().get(SPREADSHEET_ID)
				.setIncludeGridData(false)
				.execute();

		List<Sheet> sheetList = response1.getSheets();

		List<String> sheetNames = new ArrayList<>();

		for (Sheet sheet : sheetList) {
			sheetNames.add(sheet.getProperties().getTitle());
		}

		return sheetNames.toArray(new String[0]);
	}

	/**
	 * Gets the note contained in cell (0,0)
	 */
	public String getPokeditorSheetType(int sheetId) throws IOException {
		return sheetsService.spreadsheets().get(SPREADSHEET_ID)
				.setIncludeGridData(true)
				.setFields("sheets/data/rowData/values/note")
				.execute()
				.getSheets()
				.get(sheetId)
				.getData()
				.get(0)
				.getRowData()
				.get(0)
				.getValues()
				.get(0)
				.getNote();
	}
	
	private int indexOf(String[] arr, String str) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	private int indexOf(List<Sheet> list, String str) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getProperties().getTitle().equals(str)) {
				return i;
			}
		}
		return -1;
	}
}

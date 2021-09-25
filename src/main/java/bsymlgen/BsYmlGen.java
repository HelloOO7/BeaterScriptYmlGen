/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsymlgen;

import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import turtleisaac.GoogleSheetsAPI;

public class BsYmlGen {

	public static final String[][] SHEET_URLS = new String[][]{
		{"BW", null},
		{"B2W2", "https://docs.google.com/spreadsheets/d/1zvLQFVdv6kbEgP9TY9yfV6ChK0qsz79E6PvF5lohnGk"}
	};

	public static void main(String[] args) {
		for (String[] sheetset : SHEET_URLS) {
			String sheetTag = sheetset[0];
			String sheetURL = sheetset[1];
			if (sheetURL == null) {
				continue;
			}

			System.out.println("Converting sheetset " + sheetTag);

			try {
				File sheetDataRoot = new File(System.getProperty("user.dir") + "/" + sheetTag);
				sheetDataRoot.mkdirs();

				GoogleSheetsAPI api = new GoogleSheetsAPI(sheetURL, ".");

				String[] sheets = api.getSheetNames();

				for (String sheet : sheets) {
					System.out.println("Converting function set " + sheet);

					List<List<String>> values = api.getSpecifiedSheet(sheet);
					List<List<CellFormat>> sheetCellFormats = api.getSpecifiedSheetCellFormats(sheet);

					int COL_OPCODE = -1;
					int COL_BSNAME = -1;
					int COL_BRIEF = -1;
					int COLID_PSPKG = -1;
					int COLID_PSNAME = -1;

					int idx = 0;
					for (Object o : values.get(0)) {
						if (o instanceof String) {
							String s = (String) o;
							switch (s) {
								case ColumnTags.CT_BRIEF:
									COL_BRIEF = idx;
									break;
								case ColumnTags.CT_BSNAME:
									COL_BSNAME = idx;
									break;
								case ColumnTags.CT_OPCODE:
									COL_OPCODE = idx;
									break;
								case ColumnTags.CT_PSPKG:
									COLID_PSPKG = idx;
									break;
								case ColumnTags.CT_PSNAME:
									COLID_PSNAME = idx;
									break;
							}

							if (s.startsWith(ColumnTags.CT_BSNAME)) {
								COL_BSNAME = idx;
							}
						}
						idx++;
					}

					List<FuncData> funcs = new ArrayList<>();

					for (int i = 1; i < values.size(); i++) {
						List<String> rowStr = values.get(i);
						List<CellFormat> rowCellFormats = sheetCellFormats.get(i);

						String opCode = rowStr.get(COL_OPCODE);
						if (!opCode.trim().isEmpty()) {
							String opcodeUnbased = opCode;
							if (opcodeUnbased.startsWith("0x")) {
								opcodeUnbased = opcodeUnbased.substring(2);
							}

							FuncData fd = new FuncData();
							fd.opCode = Integer.parseInt(opcodeUnbased, 16);
							fd.name = rowStr.get(COL_BSNAME);

							if (COL_BRIEF < rowStr.size()) {
								fd.brief = rowStr.get(COL_BRIEF);
							}

							fd.type = FuncType.identifyByColor(getAWTColor(rowCellFormats.get(COL_BSNAME).getTextFormat().getForegroundColor()));
							if (rowCellFormats.get(COL_BSNAME).getTextFormat().getItalic()) {
								fd.isConditional = true;
							}
							if (rowCellFormats.get(COL_BSNAME).getTextFormat().getBold()) {
								fd.writesCondition = true;
							}

							if (COLID_PSPKG < rowStr.size()) {
								fd.psClasspath = rowStr.get(COLID_PSPKG);
							}
							if (COLID_PSNAME < rowStr.size()) {
								fd.psName = rowStr.get(COLID_PSNAME);
							}

							for (int argIdx = COL_BSNAME + 1; argIdx < Math.min(COL_BRIEF, rowStr.size()); argIdx++) {
								Color typeColor = rowCellFormats.get(argIdx).getBackgroundColor();
								java.awt.Color col = getAWTColor(typeColor);
								String name = rowStr.get(argIdx).trim();

								ArgType t = ArgType.valueOf(col);
								if (t == null) {
									if (name.isEmpty()) {
										break;
									} else {
										t = ArgType.U16_H;
									}
								}
								FuncData.MethodArgument arg = new FuncData.MethodArgument();

								arg.name = name;

								if (arg.name.isEmpty()) {
									arg.name = null;
								}

								arg.type = t;
								fd.arguments.add(arg);
							}
							funcs.add(fd);
						}
					}

					PrintStream out = new PrintStream(new File(sheetDataRoot + "/" + sheet + ".yml"));
					for (FuncData fd : funcs) {
						if (!fd.name.trim().isEmpty()) {
							out.println(fd.toString());
						}
					}
					out.close();
				}
			} catch (IOException | GeneralSecurityException ex) {
				Logger.getLogger(BsYmlGen.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public static java.awt.Color getAWTColor(Color color) {
		int red = (int) (color.getRed() == null ? 0 : color.getRed() * 255);
		int blue = (int) (color.getBlue() == null ? 0 : color.getBlue() * 255);
		int green = (int) (color.getGreen() == null ? 0 : color.getGreen() * 255);

		return new java.awt.Color(red, green, blue);
	}
}

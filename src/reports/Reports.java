package reports;

import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import storage.Budget;
import storage.BudgetTransaction;
import dataaudit.DataAudit.AuditFinding;
import dataaudit.DataAudit.FindingType;

/**
 * Formats reports to console or files so that everything is readable.
 *
 * @author Chime
 * @author Lhamu
 * @author Rehmil
 * @author Tasfia
 */

public final class Reports {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BOLD = "\u001B[1m";
	public static final String ANSI_UNDERLINE = "\u001B[4m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_WHITE = "\u001B[37m";
    public static final String BG_BLACK = "\u001B[40m";

	
	/**
	 * Generates a formatted annual summary analysis report for the console.
	 * The report will have monthly income and expense totals, category totals,
	 * and overall budget performance (surplus or deficit).
	 *
	 * @param budget The annual budget being analyzed.
	 * @return A formatted annual summary report.
	 * @author Rehmil
	 * @author Lhamu
	 * 
	 * @bug If an amount was over the maximum int value, the report produced a negative
	 * 		income total and an incorrect deficit in the report (PFM-124).
	 */
	public static String formatSummaryAnalysis(Budget budget) {

		// --- CALCULATIONS FOR REPORT ---
		
		String[] months = {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};

		// This is how we store monthly expenses and income. Value at
		// monthlyExpenses[0] is January's monthly expenses. The same
		// goes for monthlyIncome.
		long[] monthlyExpenses = new long[12];
		long[] monthlyIncome = new long[12];
        long[] monthlyNet = new long[12];

		long totalExpenses = 0;
		long totalIncome = 0;
		
		// We track the expenses and income in terms of categories the same way
		// as we do by months.
		String[] expensesCategories = {
			"Home", "Utilities", "Food", "Appearance", "Work", "Education",
			"Transportation", "Entertainment", "Prof. Services", "Other"
		};
		
		String[] incomeCategories = {
			"Compensation", "Allowance", "Investments", "Other"
		};
		
		long[] incomeCategoryTotals = new long[incomeCategories.length];
		long[] expensesCategoryTotals = new long[expensesCategories.length];
		
		List<BudgetTransaction> transactions = budget.transactions();
		int n = transactions.size();
		int month;
		
		for (int i = 0; i < n; ++i) {

			// For every line on the original CSV (transaction), we store its month by
			// storing the transaction's month as a number then subtracting 1 so it matches
			// each month's index in the months array.
			BudgetTransaction t = transactions.get(i);
			month = (t.date().getMonthValue()) - 1;
			
			// Calculations for monthly income totals. ---
			// Every time the transaction has a + amount, we add the value to
			// the corresponding month.
			if (t.amount() > 0) {
				monthlyIncome[month] += t.amount();

				// Calculations for income category totals. ---
				// Go through the incomeCategories array to see which index place this
				// amount will be in. Once we find it and store it, we leave the loop.
				for (int j = 0; j < incomeCategories.length; ++j) {
					if (t.category().equals(incomeCategories[j])) {
						incomeCategoryTotals[j] += t.amount();
						break;
					}
				}
			}
			
			// Calculations for monthly income totals. ---
			// Repeat for expenses.
			else if (t.amount() < 0) {
				monthlyExpenses[month] += t.amount();
				
				// Calculations for expenses category totals. ---
				// Repeat for expenses.
				for (int j = 0; j < expensesCategories.length; ++j) {
					if (t.category().equals(expensesCategories[j])) {
						expensesCategoryTotals[j] += t.amount();
						break;
					}
				}
			}
		}
		
		// Calculations for monthly net. ---
		for (int i = 0; i < 12; ++i) {
            monthlyNet[i] = monthlyIncome[i] + monthlyExpenses[i];
		}
		
		
		// Calculations for the year's income and expenses totals. ---
		for (int i = 0; i < 12; ++i) {
			totalExpenses += monthlyExpenses[i];
			totalIncome += monthlyIncome[i];
		}
		
		
		// --- FORMATTING OF REPORT ---
				
		// Keeps the columns with money consistent. Allowing for
		// our report to handle amounts like $2,000,000,000,000.
		int MONEY_COLUMN_WIDTH = 20;
		
		StringBuilder report = new StringBuilder();
        String newLine = System.lineSeparator();

        String headerLine = "======================================================================================";
        String smallLine = "----------------------------------------";
        String largeLine = "--------------------------------------------------------------------------------------";

        long balance = totalIncome + totalExpenses;

        report.append(ANSI_YELLOW);
        
        String status;
        String statusColor;
        if (balance > 0) {
            status = "SURPLUS";
            statusColor = ANSI_GREEN;
        } else if (balance < 0) {
            status = "DEFICIT";
            statusColor = ANSI_RED;
        } else {
            status = "BALANCED";
            statusColor = ANSI_BLUE;
        }

        // Report header ---
		report.append(headerLine).append(newLine + ANSI_BOLD);
		report.append("                                        P F M").append(newLine);
		report.append("                               PERSONAL FINANCE MANAGER")
			  .append(newLine);
		
		report.append("                               ANNUAL STATEMENT // " + budget.year())
			  .append(newLine);
		
        report.append(ANSI_RESET + ANSI_YELLOW + headerLine + ANSI_RESET).append(newLine).append(newLine + ANSI_RESET);

        // Financial summary --
        int summaryWidth = smallLine.length();
        int padding = (headerLine.length() - summaryWidth) / 2;
        String summaryPadding = " ".repeat(padding);

        String summaryRowFormat =
            "| %-15s %" + MONEY_COLUMN_WIDTH + "s |%n";

        report.append("                               << FINANCIAL SUMMARY >>")
              .append(newLine);

        report.append(summaryPadding).append(smallLine).append(newLine);

        report.append(summaryPadding)
              .append(String.format(
                  summaryRowFormat,
                  "Income",
                  String.format("$%,d", totalIncome)
              ));

        report.append(summaryPadding)
              .append(String.format(
                  summaryRowFormat,
                  "Expenses",
                  String.format("$%,d", Math.abs(totalExpenses))
              ));

        report.append(summaryPadding).append("|--------------------------------------|").append(newLine);

        report.append(summaryPadding).append(String.format(
        	   summaryRowFormat,
               "Balance",
               String.format("%s$%,d",
            		  balance < 0 ? "-" : "",
                      Math.abs(balance))
         ));

        report.append(summaryPadding).append(smallLine).append(newLine);

        // Monthly overview --
		String monthlyRowFormat =
			    "| %-13s | %" + MONEY_COLUMN_WIDTH + "s | %" +
			    MONEY_COLUMN_WIDTH + "s | %" +
			    MONEY_COLUMN_WIDTH + "s |%n";
		
        report.append(newLine).append(newLine);
        report.append("                                << MONTHLY OVERVIEW >>").append(newLine);
        report.append(largeLine).append(newLine);
        report.append(String.format(monthlyRowFormat, "Month", "Income", "Expenses", "Net"));
        report.append("|---------------|----------------------|----------------------|----------------------|")
              .append(newLine);

        for (int i = 0; i < 12; i++) {

            report.append(String.format(
                    monthlyRowFormat,
                    months[i],
                    String.format("$%,d", monthlyIncome[i]),
                    String.format("$%,d",
                            Math.abs(monthlyExpenses[i])),
                    String.format("%s$%,d",
                            monthlyNet[i] < 0 ? "-" : "",
                            Math.abs(monthlyNet[i]))));
        }
        
        report.append(largeLine).append(newLine);

        // Category details --
        report.append(newLine).append(newLine);
        report.append("                                << CATEGORY DETAILS >>").append(newLine);
        report.append(summaryPadding).append(smallLine).append(newLine);

        // Income categories --
        
        String categoryRowFormat = "| %-16s%" + MONEY_COLUMN_WIDTH + "s |%n";
        
        report.append(summaryPadding).append("|INCOME                                |").append(newLine);

        for (int i = 0; i < incomeCategories.length; i++) {
            report.append(summaryPadding).append(String.format(categoryRowFormat,
                    incomeCategories[i],
                    String.format("$%,d",
                            incomeCategoryTotals[i])));
        }

        report.append(summaryPadding).append("|--------------------------------------|").append(newLine);

        // Expense categories --
        report.append(summaryPadding).append("|EXPENSES                              |").append(newLine);
        
        for (int i = 0; i < expensesCategories.length; i++) {
            report.append(summaryPadding).append(String.format(categoryRowFormat,
                    expensesCategories[i],
                    String.format("$%,d",
                            expensesCategoryTotals[i])));
        }

        // Final balance --
        report.append(summaryPadding).append(smallLine).append(newLine).append(newLine);

        report.append(summaryPadding).append(String.format("%-25s %14s%n",
                "TOTAL BALANCE",
                String.format("%s$%,d",
                        balance < 0 ? "-" : "",
                        Math.abs(balance))));

        report.append(summaryPadding).append(String.format("%-25s        %16s%n",
        			  "STATUS", ANSI_UNDERLINE + statusColor + status +  ANSI_RESET + ANSI_YELLOW)).append(newLine);

        report.append(headerLine).append(newLine + ANSI_BOLD);
        report.append("                                   END OF STATEMENT")
              .append(newLine).append(newLine + ANSI_RESET);

        return report.toString();
	}

	
    /**
	 * Generates a report that describes all the validation
	 * errors and warnings in a budget file.
     *
     * @param validationErrors The warnings and errors generated by the
     * 						   Validation module.
     * @return A formatted validation report.
     * @author Chime
     */
	public static String formatValidationReport(List<String> validationErrors) {
    // Create a StringBuilder to construct the report
		StringBuilder report = new StringBuilder();
		String newline = System.lineSeparator();
		String line = "----------------------------------------";

		// Report header 
		report.append(line).append(newline);
		report.append("             PFM VALIDATION REPORT").append(newline);
		report.append("           Personal Finance Manager").append(newline);
		report.append(line).append(newline).append(newline);

        // Validation status
		String status;
		int issueCount = 0;
		
		if (validationErrors == null) {
			status = "UNAVAILABLE";
		} else if (validationErrors.isEmpty()) {
			status = "NO ISSUES FOUND";
		} else {
			status = "ISSUES DETECTED";
			issueCount = validationErrors.size();
		}
		
		// Summary section
		report.append("VALIDATION SUMMARY").append(newline);
		report.append(line).append(newline);
		report.append(String.format("%-22s : %s%n",
									"Status", status));
		
		if (validationErrors != null) {
			report.append(String.format("%-22s : %d%n",
										"Total Issues", issueCount));
		}
		
		report.append(newline);
		
		// Validation details
		if (validationErrors == null) {
			report.append("Validation results are unavailable.")
				.append(newline);
		
		} else if (validationErrors.isEmpty()) {
			report.append("No validation errors or warnings found.")
				.append(newline);
		
		} else {
			report.append("VALIDATION DETAILS").append(newline);
			report.append(line).append(newline);
			
			for (int i = 0; i < validationErrors.size(); i++) {
		
				String issue = validationErrors.get(i);
				
				if (issue == null || issue.isBlank()) {
					issue = "No description provided.";
				}
				
				report.append(String.format("%2d | %s%n", i + 1, issue));
			}
		}
		
		//Report footer
		report.append(newline);
		report.append(line).append(newline);
		report.append("              END OF REPORT").append(newline);
		report.append(line);
		
		return report.toString();
	}


	
	/** Lines that frame the report: %85s makes 85 spaces, then each space is replaced. */
	private static final String AUDIT_BANNER = String.format("%85s", "").replace(' ', '=');
	private static final String AUDIT_LINE = String.format("%85s", "").replace(' ', '-');

	/** Table columns: #, Date, Category, Amount ($), Reason. */
	private static final String AUDIT_ROW = "%3s  %-10s  %-21s %10s  %s%n";

	/**
	 * Creates a report that contains the duplicates and
	 * anomalous entries identified during the data audit.
	 *
	 * @param findings The audit findings generated by the Data Audit module.
	 * @return A formatted audit report.
	 * @author Tasfia
	 */
	public static String formatAuditReport(List<AuditFinding> findings) {
		StringBuilder sb = new StringBuilder();

		// Title box. %51s right-aligns the 17-letter title so it ends at column 51,
		// which centers it in the 85-character report.
		sb.append(String.format("%s%n%51s%n%s%n", AUDIT_BANNER, "DATA AUDIT REPORT", AUDIT_BANNER));

		// No audit results were passed in.
		if (findings == null) {
			sb.append(String.format("Audit results are unavailable.%n"));
			sb.append(AUDIT_BANNER);
			return sb.toString();
		}

		// Count each type of finding, skipping null findings and null types.
		int duplicateCount = 0;
		int anomalyCount = 0;
		for (AuditFinding finding : findings) {
			if (finding == null || finding.type() == null) {
				continue;
			}
			switch (finding.type()) {
				case DUPLICATE -> duplicateCount++;
				case ANOMALY -> anomalyCount++;
			}
		}

		// The audit ran but found nothing.
		if (duplicateCount + anomalyCount == 0) {
			sb.append(String.format("No duplicates or anomalies were found.%n"));
			sb.append(AUDIT_BANNER);
			return sb.toString();
		}

		// The ternary operator picks the singular or plural word.
		sb.append(String.format("Total findings: %d  (%d %s, %d %s)%n",
				duplicateCount + anomalyCount,
				duplicateCount, (duplicateCount == 1) ? "duplicate" : "duplicates",
				anomalyCount, (anomalyCount == 1) ? "anomaly" : "anomalies"));

		if (duplicateCount > 0) {
			appendFindingSection(sb, findings, FindingType.DUPLICATE);
		}
		if (anomalyCount > 0) {
			appendFindingSection(sb, findings, FindingType.ANOMALY);
		}

		sb.append(AUDIT_BANNER);
		return sb.toString();
	}

	/**
	 * Adds one type of audit finding to the report as a table
	 * with lined-up columns and right-aligned amounts.
	 *
	 * @param sb The StringBuilder used to create the report.
	 * @param findings The list of audit findings.
	 * @param type The type of findings to display.
	 * @author Tasfia
	 */
	private static void appendFindingSection(StringBuilder sb, List<AuditFinding> findings,
			FindingType type) {

		// switch expression: no default is needed because both enum values are covered.
		String heading = switch (type) {
			case DUPLICATE -> "DUPLICATES";
			case ANOMALY -> "ANOMALIES";
		};

		// Heading, then the column titles between two dashed lines.
		sb.append(String.format("%n%s%n%s%n", heading, AUDIT_LINE));
		sb.append(String.format(AUDIT_ROW, "#", "Date", "Category", "Amount ($)", "Reason"));
		sb.append(String.format("%s%n", AUDIT_LINE));

		int number = 0;
		for (AuditFinding finding : findings) {
			if (finding == null || finding.type() != type) {
				continue;
			}
			number++;

			// Fallback text so the report never shows the word "null".
			String entry = (finding.entry() == null) ? "(no entry given)" : finding.entry();
			String reason = (finding.reason() == null) ? "(no reason given)" : finding.reason();

			// By default, show the whole entry in the Category column.
			String date = "-";
			String category = entry;
			String amount = "";

			if (entry.matches("BudgetTransaction\\[date=.*, category=.*, amount=.*\\]")) {
				// A duplicate looks like:
				// BudgetTransaction[date=2025-03-09, category=Transportation, amount=-127]
				// Remove "BudgetTransaction[" and "]", split into the three fields,
				// then keep the part after each "=".
				String[] fields = entry.replaceAll("BudgetTransaction\\[|\\]", "").split(", ");
				date = fields[0].split("=")[1];
				category = fields[1].split("=")[1];
				amount = fields[2].split("=")[1];
			} else {
				// An anomaly only gives the category. Its amount is the 2nd word of
				// a reason like "Amount 3200 is out of normal range".
				String[] words = reason.split(" ");
				if (words.length > 1 && words[0].equals("Amount")) {
					amount = words[1];
				}
			}

			// Add commas to the amount (3200 -> 3,200). If it is not a number, show "-".
			String amountText;
			try {
				amountText = String.format("%,d", Integer.parseInt(amount));
			} catch (NumberFormatException e) {
				amountText = "-";
			}

			sb.append(String.format(AUDIT_ROW, number, date, category, amountText, reason));
		}
	}

	
    
    /**
     * Displays a report to the console.
     *
     * @param report The report to display.
     * @author Lhamu
     */
    public static void displayReport(String report) {
    	System.out.println(report);
    }


	
    /**
     * Exports a report to a file.
     *
     * @param report The report to export.
     * @param fileName The name of the output file.
     * @author Lhamu
     */
    public static void exportReport(String report, String fileName) {
    	try (BufferedWriter writer =
                Files.newBufferedWriter(Path.of(fileName))) {

            writer.write(report);

        } catch (IOException e) {
            System.err.println("Error exporting report.\n");
            return;
        }

        System.out.println("Report exported successfully.\n");
    	}
    


    /**
     * Creates a message that indicates that the current
     * reporting operation was cancelled. This method is not
     * used internally in our module but is meant to be used
     * by other modules.
     *
     * @param operation Name of the cancelled operation.
     * @return A cancellation message.
     * @author Rehmil
     */
    public static String cancelStatement(String operation) {
		return "Operation Cancelled: " + operation;
	}

	
}

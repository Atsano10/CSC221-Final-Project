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
	
	/**
	 * Private constructor so Reports class cannot be instantiated.
	 */
	
	private Reports() {}
	
	/**
	 * Generates a formatted annual summary analysis report. The report will have
	 * monthly income and expense totals, category totals, and overall budget
	 * performance (surplus or deficit).
	 *
	 * @param budget The annual budget being analyzed.
	 * @return A formatted annual summary report.
	 * @author Rehmil
	 * @author Lhamu
	 */
	public static String formatSummaryAnalysis(Budget budget) {
		
		// --- CALCULATIONS ---
		String[] months = {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};
		
		int[] monthlyExpenses = new int[12];
		int[] monthlyIncome = new int[12];
		
		int totalExpenses = 0;
		int totalIncome = 0;
		
		
		String[] expensesCategories = {
			"Home", "Utilities", "Food", "Appearance", "Work", "Education",
			"Transportation", "Entertainment", "Professional Services"
		};
		
		String[] incomeCategories = {
			"Compensation", "Allowance", "Investments"
		};
		
		int[] incomeCategoryTotals = new int[incomeCategories.length];
		int[] expensesCategoryTotals = new int[expensesCategories.length];
		
		
		List<BudgetTransaction> transactions = budget.transactions();
		int n = transactions.size();
		int month;

		
		for (int i = 0; i < n; ++i) {
			
			BudgetTransaction t = transactions.get(i);
			month = (t.date().getMonthValue()) - 1;
			
			// Calculations for monthly income totals. ---
			if (t.amount() > 0) {
				monthlyIncome[month] += t.amount();

				// Calculations for income category totals. ---
				for (int j = 0; j < incomeCategories.length; ++j) {
					if (t.category().equals(incomeCategories[j])) {
						incomeCategoryTotals[j] += t.amount();
						break;
					}
				}
			}
			
			// Calculations for monthly income totals. ---
			else if (t.amount() < 0) {
				monthlyExpenses[month] += t.amount();
				
				// Calculations for expenses category totals. ---
				for (int j = 0; j < expensesCategories.length; ++j) {
					if (t.category().equals(expensesCategories[j])) {
						expensesCategoryTotals[j] += t.amount();
						break;
					}
				}
			}
		}
		
		
		// Calculations for monthly totals. ---
		for (int i = 0; i < 12; ++i) {
			totalExpenses += monthlyExpenses[i];
			totalIncome += monthlyIncome[i];
		}
		
		// --- FORMATTING ---
		StringBuilder report = new StringBuilder();
		String newLine = System.lineSeparator();
		
		report.append("======== ANNUAL SUMMARY ANALYSIS REPORT ========")
			  .append(newLine)
			  .append(newLine);
		
		report.append("Year: ").append(budget.year())
			  .append(newLine).append(newLine).append(newLine);
		
		// Formatting monthly totals. ---
		report.append("MONTHLY TOTALS --------------").append(newLine).append(newLine);
		
		for (int i = 0; i < 12; ++i) {
			report.append(months[i]).append(newLine);
			
			// Format monthly income. ---
			report.append("Income: $").append(monthlyIncome[i]).append(newLine);
			
			// Format monthly expenses. ---
			report.append("Expenses: $").append(Math.abs(monthlyExpenses[i])).append(newLine);
			
			// Format net monthly income. --- 
			int net = monthlyIncome[i] + monthlyExpenses[i];
			report.append("Net: ");
			if (net < 0) {
				report.append("-$").append(Math.abs(net)).append(newLine);
			}
			else {
				report.append("$").append(net).append(newLine);
			}
			
			report.append(newLine);
		}
		
		report.append(newLine);
		
		// Format category totals. ---
	    report.append("CATEGORY TOTALS ----------------");
	    report.append(newLine).append(newLine);

	    // Format income category totals. ---
	    report.append("Income by Category:");
	    report.append(newLine);

	    for (int i = 0; i < incomeCategories.length; i++) {
	        report.append(incomeCategories[i]);
	        report.append(": $");
	        report.append(incomeCategoryTotals[i]);
	        report.append(newLine);
	    }

	    report.append(newLine);

	    // Format expenses category totals. ---
	    report.append("Expenses by Category:");
	    report.append(newLine);

	    for (int i = 0; i < expensesCategories.length; i++) {
	        report.append(expensesCategories[i]);
	        report.append(": $");
	        report.append(Math.abs(expensesCategoryTotals[i]));
	        report.append(newLine);
	    }

	    report.append(newLine).append(newLine);

	    // Format overall performance. ---
	    report.append("OVERALL PERFORMANCE ----------------");
	    report.append(newLine).append(newLine);

	    // Format total income. ---
	    report.append("Total Income: $");
	    report.append(totalIncome);
	    report.append(newLine);

	    // Format total expenses. ---
	    report.append("Total Expenses: $");
	    report.append(Math.abs(totalExpenses));
	    report.append(newLine);

	    // Determine + format if surplus or deficit. ---
	    int net = totalIncome + totalExpenses;

	    if (net > 0) {
	        report.append("Surplus: $");
	        report.append(net);
	        report.append(newLine);
	    }
	    else if (net < 0) {
	        report.append("Deficit: $");
	        report.append(Math.abs(net));
	        report.append(newLine);
	    }
	    else {
	        report.append("Balanced: $0");
	        report.append(newLine);
	    }
		report.append(newLine);
		report.append("=========================================");
		
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
    
    report.append("============== VALIDATION REPORT ==============")
			  .append(newline);
    
    // Check whether the error list is null or empty
		if (validationErrors == null) {
			report.append("Validation results are unavailable.")
				  .append(newline);
		}
		
		else if (validationErrors.isEmpty()) {
			report.append("No validation errors or warnings found.")
			      .append(newline);
	
		} 
		
		else {
			report.append("Total Validation Issues Found: ")
				  .append(validationErrors.size())
				  .append(newline);
     
      for (int i = 0; i < validationErrors.size(); i++) {
				report.append(i + 1)
					  .append(". ")
					  .append(validationErrors.get(i))
					  .append(newline);
			}
		}

		report.append(newline)
			  .append("==============================================");
		return report.toString();
	}



	/**
	 * Banner line used at the bottom of the data audit report.
	 */
	private static final String AUDIT_BANNER = "=================================================";

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

		// Header
		sb.append(String.format("=============== DATA AUDIT REPORT ===============%n"));

		// No audit results were passed in.
		if (findings == null) {
			sb.append(String.format("Audit results are unavailable.%n"));
			sb.append(AUDIT_BANNER);
			return sb.toString();
		}

		// The audit ran but found nothing.
		if (findings.isEmpty()) {
			sb.append(String.format("No duplicates or anomalies were found.%n"));
			sb.append(AUDIT_BANNER);
			return sb.toString();
		}

		// Count the duplicates and anomalies, skipping null findings and null types.
		int duplicateCount = 0;
		int anomalyCount = 0;
		for (AuditFinding finding : findings) {
			if (finding == null || finding.type() == null) {
				continue;
			}
			if (finding.type() == FindingType.DUPLICATE) {
				duplicateCount++;
			} else if (finding.type() == FindingType.ANOMALY) {
				anomalyCount++;
			}
		}

		// Use singular or plural words so the summary line is grammatically correct.
		String duplicateWord;
		if (duplicateCount == 1) {
			duplicateWord = "duplicate";
		} else {
			duplicateWord = "duplicates";
		}

		String anomalyWord;
		if (anomalyCount == 1) {
			anomalyWord = "anomaly";
		} else {
			anomalyWord = "anomalies";
		}

		sb.append(String.format("Total findings: %d (%d %s, %d %s)%n",
				duplicateCount + anomalyCount, duplicateCount, duplicateWord,
				anomalyCount, anomalyWord));

		if (duplicateCount > 0) {
			appendFindingSection(sb, findings, FindingType.DUPLICATE, "DUPLICATES");
		}

		if (anomalyCount > 0) {
			appendFindingSection(sb, findings, FindingType.ANOMALY, "ANOMALIES");
		}

		sb.append(AUDIT_BANNER);
		return sb.toString();
	}


	
	/**
	 * Adds a numbered section of audit findings to the report.
	 *
	 * @param sb The StringBuilder used to create the report.
	 * @param findings The list of audit findings.
	 * @param type The type of findings to display.
	 * @param heading The heading of the report section.
	 * @author Tasfia
	 */
	private static void appendFindingSection(StringBuilder sb, List<AuditFinding> findings,
			FindingType type, String heading) {

		// Blank line, then the section heading.
		sb.append(String.format("%n%s%n", heading));

		// Numbering starts at 1 because number is increased before each finding is printed.
		int number = 0;
		for (AuditFinding finding : findings) {
			if (finding == null || finding.type() != type) {
				continue;
			}

			number++;

			// Use fallback text so the report never shows the word "null".
			String entry = finding.entry();
			if (entry == null) {
				entry = "(no entry given)";
			}

			String reason = finding.reason();
			if (reason == null) {
				reason = "(no reason given)";
			}

			sb.append(String.format("  %d. %s%n", number, entry));
			sb.append(String.format("     Reason: %s%n", reason));
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
            System.err.println("Error exporting report.");
            return;
        }

        System.out.println("Report exported successfully.");
    	}
    


    /**
     * Creates a message that indicates that the current
     * reporting operation was cancelled.
     *
     * @param operation Name of the cancelled operation.
     * @return A cancellation message.
     * @author Rehmil
     */
    public static String cancelStatement(String operation) {
		return "Operation Cancelled: " + operation;
	}

	
}


package telran.employees;

import java.util.*;
import java.util.stream.Collectors;

import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;
import telran.view.SystemInputOutput;

import static telran.employees.CompanyConfigProperties.*;

public class CompanyApplItems {
	static Company company;
	static HashSet<String> departments;
	
public static List<Item> getCompanyItems(Company company,
		HashSet<String> departments) {
	CompanyApplItems.company = company;
	CompanyApplItems.departments = departments;
	Item[] addEmployeeList = {
			Item.of("add WageEmployee", CompanyApplItems::addEmployee)	,
			Item.of("add Manager", CompanyApplItems::addEmployee),
			Item.of("add SalesPerson", CompanyApplItems::addEmployee)
	};
	Menu submenu = new Menu("add employee", addEmployeeList);
	Item[] items = {
		Item.of("add employee", io -> submenu.perform(new SystemInputOutput())),
		Item.of("display employee data", CompanyApplItems::getEmployee),
		Item.of("remove employee", CompanyApplItems::removeEmployee),
		Item.of("display department budget", CompanyApplItems::getDepartmentBudget),
		Item.of("display departments", CompanyApplItems::getDepartments),
		Item.of("display managers with most factor", CompanyApplItems::getManagersWithMostFactor),
	};
	return new ArrayList<>(List.of(items));
	
}
static void addEmployee(InputOutput io) {
	Employee empl = readEmployee(io);
	String type = io.readStringOptions("Enter employee type",
			"Wrong Employee Type", new HashSet<String>
	(List.of("WageEmployee", "Manager", "SalesPerson")));
	Employee result = switch(type) {
	case "WageEmployee" -> getWageEmployee(empl, io);
	case "Manager" -> getManager(empl, io);
	case "SalesPerson" -> getSalesPerson(empl, io);
	default -> null;
	};
	company.addEmployee(result);
	io.writeLine("Employee has been added");
}
private static Employee getSalesPerson(Employee empl, InputOutput io) {
	WageEmployee wageEmployee = (WageEmployee) getWageEmployee(empl, io);
	float percents = io.readNumberRange("Enter percents", "Wrong percents value", MIN_PERCENT, MAX_PERCENT).floatValue();
	long sales = io.readNumberRange("Enter sales", "Wrong sales value", MIN_SALES, MAX_SALES).longValue();
	return new SalesPerson(empl.getId(), empl.getBasicSalary(), empl.getDepartment(),
			wageEmployee.getHours(), wageEmployee.getWage(),
			percents, sales);
}
private static Employee getManager(Employee empl, InputOutput io) {
	
	float factor = io.readNumberRange("Enter factor",
			"Wrong factor value", MIN_FACTOR, MAX_FACTOR).floatValue();
	return new Manager(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), factor );
}
private static Employee getWageEmployee(Employee empl, InputOutput io) {
	
	int hours = io.readNumberRange("Enter working hours",
			"Wrong hours value", MIN_HOURS, MAX_HOURS).intValue();
	int wage = io.readNumberRange("Enter hour wage",
			"Wrong wage value", MIN_WAGE, MAX_WAGE).intValue();;
	return new WageEmployee(empl.getId(), empl.getBasicSalary(), empl.getDepartment(), hours, wage);
}
private static Employee readEmployee(InputOutput io) {
	
	long id = readEmployeeId(io);
	int basicSalary = io.readNumberRange("Enter basic salary", "Wrong basic salary", MIN_BASIC_SALARY,
			MAX_BASIC_SALARY).intValue();
	String department = readDepartment(io);
	return new Employee(id, basicSalary, department);
}
private static String readDepartment(InputOutput io) {
	return io.readStringOptions("Enter department " + departments, "Wrong department", departments);
}
private static long readEmployeeId(InputOutput io) {
	return io.readNumberRange("Enter id value", "Wrong id value", MIN_ID, MAX_ID).longValue();
}
static void getEmployee(InputOutput io) {
	long id = readEmployeeId(io);
	Employee empl = company.getEmployee(id);
	String line = empl == null ? "no employee with the entered ID"
			: empl.getJSON();
	io.writeLine(line);
}
static void removeEmployee(InputOutput io) {
	long id = readEmployeeId(io);
	Employee empl = company.removeEmployee(id);
	io.writeLine(empl);
	io.writeLine("has been removed from the company\n");
}
static void getDepartmentBudget(InputOutput io) {
	String department = readDepartment(io);
	int budget = company.getDepartmentBudget(department);
	String line = budget == 0 ? "no employees woring in entered department" :
		"Budget of enetered department is " + budget;
	io.writeLine(line);
}
static void getDepartments(InputOutput io) {
	String [] departments = company.getDepartments();
	String line = departments.length == 0 ? "no employees" : 
		String.join("\n", departments);
	io.writeLine(line);
}
static void getManagersWithMostFactor(InputOutput io) {
	Manager[] managers = company.getManagersWithMostFactor();
	String line = managers.length == 0 ? "no managers" :
		Arrays.stream(managers).map(Employee::getJSON)
		.collect(Collectors.joining("\n"));
	io.writeLine(line);
}
}

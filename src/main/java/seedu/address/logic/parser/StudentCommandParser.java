package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ATTENDANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CCA;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMMENTCOMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAILSTUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GRADE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HOMEWORK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HOMEWORKDONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IMAGESTUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEXNUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENTNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONESTUDENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SCORE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TEST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEIGHTAGE;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.student.StudentAddCommand;
import seedu.address.logic.commands.student.StudentCommand;
import seedu.address.logic.commands.student.StudentCommentCommand;
import seedu.address.logic.commands.student.StudentDeleteCommand;
import seedu.address.logic.commands.student.StudentGradeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Class;
import seedu.address.model.person.Comment;
import seedu.address.model.person.Email;
import seedu.address.model.person.Image;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Sex;
import seedu.address.model.person.student.Attendance;
import seedu.address.model.person.student.Cca;
import seedu.address.model.person.student.Homework;
import seedu.address.model.person.student.IndexNumber;
import seedu.address.model.person.student.ParentName;
import seedu.address.model.person.student.Student;
import seedu.address.model.person.student.Test;
import seedu.address.model.tag.Tag;

/**
 * StudentCommandParser that parses commands starting with "student"
 */
public class StudentCommandParser implements Parser<StudentCommand> {

    public static final String HELP_MESSAGE = "Student command has to include a class and action.\n"
            + StudentCommand.MESSAGE_USAGE;
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<class>\\S+)(?<arguments>.*)");

    /**
     * Parse the command into their respective prefixes
     * @param args the command input by user
     * @return A StudentCommand
     * @throws ParseException
     */
    public StudentCommand parse(String args) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HELP_MESSAGE));
        }

        final String studentClass = matcher.group("class");
        final String arguments = matcher.group("arguments");

        ArgumentMultimap argMultimapAdd =
                ArgumentTokenizer.tokenize(arguments, PREFIX_ADD, PREFIX_NAME, PREFIX_INDEXNUMBER, PREFIX_SEX,
                        PREFIX_PARENTNAME, PREFIX_AGE, PREFIX_IMAGESTUDENT, PREFIX_EMAILSTUDENT,
                        PREFIX_PHONESTUDENT, PREFIX_CCA, PREFIX_TEST, PREFIX_ATTENDANCE, PREFIX_HOMEWORK,
                        PREFIX_SCORE, PREFIX_DEADLINE, PREFIX_WEIGHTAGE);

        ArgumentMultimap argMultimapDelete =
                ArgumentTokenizer.tokenize(arguments, PREFIX_DELETE, PREFIX_NAME, PREFIX_INDEXNUMBER, PREFIX_SEX,
                        PREFIX_PARENTNAME, PREFIX_AGE, PREFIX_IMAGESTUDENT, PREFIX_EMAILSTUDENT,
                        PREFIX_PHONESTUDENT, PREFIX_CCA, PREFIX_TEST, PREFIX_ATTENDANCE, PREFIX_HOMEWORK);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(arguments, PREFIX_COMMENTCOMMAND, PREFIX_COMMENT, PREFIX_ADD,
                        PREFIX_NAME, PREFIX_INDEXNUMBER, PREFIX_SEX,
                        PREFIX_PARENTNAME, PREFIX_AGE, PREFIX_IMAGESTUDENT, PREFIX_EMAILSTUDENT,
                        PREFIX_PHONESTUDENT, PREFIX_CCA, PREFIX_TEST, PREFIX_ATTENDANCE, PREFIX_HOMEWORK);
        ArgumentMultimap argMultimapGrade =
                ArgumentTokenizer.tokenize(arguments, PREFIX_GRADE, PREFIX_INDEXNUMBER, PREFIX_TEST,
                        PREFIX_HOMEWORK, PREFIX_SCORE, PREFIX_DEADLINE, PREFIX_WEIGHTAGE, PREFIX_HOMEWORKDONE);

        if (argMultimapAdd.getValue(PREFIX_ADD).isPresent()) {
            return addCommand(studentClass, argMultimapAdd);
        } else if (argMultimapDelete.getValue(PREFIX_DELETE).isPresent()) {
            return deleteCommand(studentClass, argMultimapDelete);
        } else if (argMultimap.getValue(PREFIX_COMMENTCOMMAND).isPresent()) {
            return commentCommand(studentClass, argMultimap);
        } else if (argMultimapGrade.getValue(PREFIX_GRADE).isPresent()) {
            return gradeCommand(studentClass, argMultimapGrade);
        } else {
            //Rest of logic (Need to edit)
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HELP_MESSAGE));
        }
    }

    /**
     * Function to parse the "student class add" command
     * @param studentClass class of student
     * @param argMultimap mapper for each prefix
     * @return A StudentAddCommand
     * @throws ParseException
     */
    private StudentAddCommand addCommand(String studentClass, ArgumentMultimap argMultimap) throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_INDEXNUMBER, PREFIX_SEX)
                || !argMultimap.getPreamble().isEmpty()
                || studentClass.length() == 0) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, StudentAddCommand.MESSAGE_USAGE));
        }
        Class sc = ParserUtil.parseStudentClass(studentClass);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        IndexNumber indexNumber = ParserUtil.parseIndexNumber(argMultimap.getValue(PREFIX_INDEXNUMBER).get());
        Sex sex = ParserUtil.parseSex(argMultimap.getValue(PREFIX_SEX).get());
        ParentName parentName = ParserUtil.parseParentName(argMultimap.getValue(PREFIX_PARENTNAME).get());
        Age age = ParserUtil.parseAge((argMultimap.getValue(PREFIX_AGE).get()));
        Image image = ParserUtil.parseImage(argMultimap.getValue(PREFIX_IMAGESTUDENT).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAILSTUDENT).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONESTUDENT).get());
        Cca cca = ParserUtil.parseCca(argMultimap.getValue(PREFIX_CCA).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Attendance attendance = ParserUtil.parseAttendance(argMultimap.getValue(PREFIX_ATTENDANCE).get());
        String score = argMultimap.getValue(PREFIX_SCORE).get();
        String deadline = argMultimap.getValue(PREFIX_DEADLINE).get();
        String weightage = argMultimap.getValue(PREFIX_WEIGHTAGE).get();
        String homeworkDone = argMultimap.getValue(PREFIX_HOMEWORKDONE).get();
        Homework homework = ParserUtil.parseHomework(argMultimap.getValue(PREFIX_HOMEWORK).get(), score, deadline,
                weightage, homeworkDone);
        Test test = ParserUtil.parseTest(argMultimap.getValue(PREFIX_TEST).get(), score, deadline, weightage);
        Comment comment = ParserUtil.parseComment(argMultimap.getValue(PREFIX_COMMENT).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Set<Homework> homeworkSet = new HashSet<>();
        homeworkSet.add(homework);
        Set<Test> testSet = new HashSet<>();
        testSet.add(test);
        Student student = new Student(name, sc, indexNumber, sex, parentName, age, image, email, phone, cca, address,
                attendance, homeworkSet, testSet, tagList, comment);

        return new StudentAddCommand(student);
    }

    /**
     * Function to parse the "student class delete" command
     * @param studentClass
     * @param argMultimap
     * @return A StudentDeleteCommand
     * @throws ParseException
     */
    private StudentCommentCommand commentCommand(String studentClass, ArgumentMultimap argMultimap)
            throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_INDEXNUMBER, PREFIX_COMMENT)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    StudentCommentCommand.MESSAGE_USAGE));
        }
        Class sc = ParserUtil.parseStudentClass(studentClass);
        IndexNumber indexNumber = ParserUtil.parseIndexNumber(argMultimap.getValue(PREFIX_INDEXNUMBER).get());
        Comment comment = ParserUtil.parseComment(argMultimap.getValue(PREFIX_COMMENT).get());
        return new StudentCommentCommand(sc, indexNumber, comment);
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public StudentDeleteCommand deleteCommand(String studentClass, ArgumentMultimap argMultimap) throws ParseException {
        try {
            Class sc = ParserUtil.parseStudentClass(studentClass);
            IndexNumber indexNumber = ParserUtil.parseIndexNumber(argMultimap.getValue(PREFIX_INDEXNUMBER).get());
            return new StudentDeleteCommand(indexNumber, sc);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Function to parse the "student class grade" command
     * @param studentClass
     * @param argMultimap
     * @return A StudentGradeCommand
     * @throws ParseException
     */
    public StudentGradeCommand gradeCommand(String studentClass, ArgumentMultimap argMultimap) throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_INDEXNUMBER, PREFIX_GRADE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    StudentGradeCommand.MESSAGE_USAGE));
        }
        Class sc = ParserUtil.parseStudentClass(studentClass);
        IndexNumber indexNumber = ParserUtil.parseIndexNumber(argMultimap.getValue(PREFIX_INDEXNUMBER).get());
        String score = argMultimap.getValue(PREFIX_SCORE).get();
        String deadline = argMultimap.getValue(PREFIX_DEADLINE).get();
        String weightage = argMultimap.getValue(PREFIX_WEIGHTAGE).get();
        String homeworkDone = argMultimap.getValue(PREFIX_HOMEWORKDONE).get();
        Test test = ParserUtil.parseTest(argMultimap.getValue(PREFIX_TEST).get(), score, deadline, weightage);
        Homework homework = ParserUtil.parseHomework(argMultimap.getValue(PREFIX_HOMEWORK).get(), score, deadline,
                weightage, homeworkDone);
        if (homework.getName() == "Insert student homework here!" && test.getName() == "Insert student test here!") {
            throw new ParseException("Please enter a test or homework!");
        } else if (test.getName() == "Insert student test here!") {
            return new StudentGradeCommand(sc, indexNumber, homework);
        } else {
            return new StudentGradeCommand(sc, indexNumber, test);
        }

    }


    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}

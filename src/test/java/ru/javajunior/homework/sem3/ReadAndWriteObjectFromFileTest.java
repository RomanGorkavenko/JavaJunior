package ru.javajunior.homework.sem3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javajunior.homework.sem3.ReadAndWriteObjectFromFile.*;

class ReadAndWriteObjectFromFileTest {

    /**
     * Временная директория.
     */
    @TempDir
    private Path tempDir;

    /**
     * Объект для записи в файл.
     */
    private Department department;

    /**
     * Поток печати.
     */
    private final PrintStream standardOut = System.out;

    /**
     * Выходной поток для записи данных в байтах
     */
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    /**
     * В методе создается объект класса {@link Department},
     * меняется директория на временную, заменяется выходной поток для записи данных.
     */
    @BeforeEach
    public void setUp() {
        department = new Department("777");
        setDirectory(tempDir);
        System.setOut(new PrintStream(byteArrayOutputStream));
    }

    /**
     * Выходной поток для записи данных, принимает дефолтное значение.
     */
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    /**
     * Тест на создание файла и записи в него объекта.
     * Успешный.
     */
    @Test
    public void should_createFileAndWriteAnObjectToIt_successfully() {

        writeObject(department);

        Path path = tempDir.resolve(getFileNameLast());

        Object actual;

        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(path))) {
            actual = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        assertAll(
                () -> assertTrue(Files.exists(path), "Файл должен существовать"),
                () -> assertInstanceOf(Department.class, actual));
    }

    /**
     * Тест на считывание объекта из файла и удаление файла.
     * Успешный.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_successfully() {

        String objectName = department.getClass().getName();
        String fileName = String.format("%s_%s", objectName, UUID.randomUUID());

        Path path = tempDir.resolve(fileName);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            objectOutputStream.writeObject(department);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Object actual = readObject(fileName);

        assertAll(
                () -> assertTrue(Files.notExists(path), "Файл не должен существовать"),
                () -> assertInstanceOf(Department.class, actual));
    }

    /**
     * Тест проверяет исключение выбрасываемое если файл не найден.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_when_thereIsNoFileWithThatName_RuntimeException() {

        String fileName = "exception";

        String expected = "Файл " + "\"" + fileName + "\"" + " не найден.";
        String actual = assertThrows(RuntimeException.class, () -> readObject(fileName)).getMessage();

        assertTrue(expected.contains(actual), "Сообщения должны совпадать");
    }

    /**
     * Тест проверяет сообщение когда выбрасывается исключение {@link OptionalDataException}.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_when_OptionalDataException_printMessageInConsole() {

        String objectName = department.getClass().getName();
        String fileName = String.format("%s_%s", objectName, UUID.randomUUID());

        Path path = tempDir.resolve(fileName);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            objectOutputStream.writeInt(456);
            objectOutputStream.writeObject(department);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readObject(fileName);

        String expected = "Попытка считать примитивные данные вместо объектов. OptionalDataException";
        String actual = byteArrayOutputStream.toString().trim();

        assertEquals(expected, actual);
    }

    /**
     * Тест проверяет сообщение когда выбрасывается исключение {@link StreamCorruptedException}.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_StreamCorruptedException_printMessageInConsole() throws IOException {

        String badData = "8888";

        String objectName = department.getClass().getName();
        String fileName = String.format("%s_%s", objectName, UUID.randomUUID());

        Path path = tempDir.resolve(fileName);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path))) {
            objectOutputStream.writeObject(department);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Files.write(path, badData.getBytes());

        readObject(fileName);

        String expected = "Данные в файле повреждены. StreamCorruptedException";
        String actual = byteArrayOutputStream.toString().trim();

        assertEquals(expected, actual);
    }

    /**
     * Тест проверяет сообщение когда выбрасывается исключение {@link InvalidClassException}.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_InvalidClassException_printMessageInConsole() {

        String fileName = "ru.javajunior.homework.sem3.Department_6652e1dd-cd5b-4017-9234-459c08203845";

        setDirectory(Path.of("data"));

        readObject(fileName);

        String expected = "С файлом что-то не так. ===> ru.javajunior.homework.sem3.Department; " +
                "local class incompatible: stream classdesc serialVersionUID = 87, local class serialVersionUID = 86";
        String actual = byteArrayOutputStream.toString().trim();

        assertEquals(expected, actual);
    }

    /**
     * Тест проверяет сообщение когда выбрасывается исключение {@link ClassNotFoundException}.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_ClassNotFoundException_printMessageInConsole() {

        String fileName = "ru.javajunior.homework.sem3.DepartmentNotFound_b503c565-e11b-4f71-98b5-3072530b8155";

        setDirectory(Path.of("data"));

        readObject(fileName);

        String expected = "Класс ru.javajunior.homework.sem3.DepartmentNotFound не найден.";
        String actual = byteArrayOutputStream.toString().trim();

        assertEquals(expected, actual);
    }

    /**
     * Тест проверяет сообщение когда выбрасывается исключение {@link GetFileNameException}.
     */
    @Test
    public void should_readObjectFromFileAndDeleteFile_GetFileNameException_printMessageInConsole() {

        String expected = "Файл не разу не был создан в этой сессии.";
        String actual = assertThrows(GetFileNameException.class, ReadAndWriteObjectFromFile::getFileNameLast).getMessage();

        assertEquals(expected, actual);
    }
}

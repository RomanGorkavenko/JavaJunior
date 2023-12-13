package ru.javajunior.homework.sem3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Класс с двумя методами для записи объекта в файл и для чтения объекта из файла.
 */
public class ReadAndWriteObjectFromFile {

    /**
     * Поле хранит имя последнего созданного файла.
     */
    private static String fileNameLast = null;

    /**
     * Наименование директории для записи файлов.
     */
    private static Path directory = Path.of("data");

    /**
     * Метод создает файл и записывает в него объект.
     * @param object принимает объект для записи.
     * @param <T> тип объекта должен имплементировать {@link Serializable}.
     */
    public static <T extends Serializable> void writeObject(T object) {

        String objectName = object.getClass().getName();
        fileNameLast = String.format("%s_%s", objectName, UUID.randomUUID());
        Path path = directory.resolve(fileNameLast);

        try {

            if (Files.notExists(directory)) {
                Files.createDirectory(directory);
            }
            Files.createFile(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Метод считывает объект из файла.
     * Обработка ошибок - {@link ClassNotFoundException}, {@link OptionalDataException},
     * {@link InvalidClassException}, {@link StreamCorruptedException}, {@link GetFileNameException}
     * @param fileName имя файла.
     * @return {@link Object}
     */
    public static Object readObject(String fileName) {

        Path path = directory.resolve(fileName);

        if (Files.notExists(path)) {
            throw new RuntimeException("Файл " + "\"" +path.getFileName().toString() + "\"" + " не найден.");
        }

        Object deserializableObject = null;

        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(path))) {

            deserializableObject = objectInputStream.readObject();
            Files.delete(path);

        } catch (ClassNotFoundException e) {
            System.out.println("Класс " + e.getMessage() + " не найден.");
        }  catch (InvalidClassException e) {
            System.out.println("С файлом что-то не так. ===> " + e.getMessage());
        } catch (StreamCorruptedException e) {
            System.out.println("Данные в файле повреждены. " + e.getClass().getSimpleName());
        } catch (OptionalDataException e) {
            System.out.println("Попытка считать примитивные данные вместо объектов. " + e.getClass().getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException("Что то пошло не так. ");
        }

        return deserializableObject;
    }

    /**
     * Метод возвращает имя последнего созданного файла.
     * @return {@link String}
     */
    public static String getFileNameLast() {
        if (fileNameLast == null) {
            throw new GetFileNameException("Файл не разу не был создан в этой сессии.");
        }
        return fileNameLast;
    }

    /**
     * Метод изменяет директорию для записи файлов.
     * @param directory путь директории {@link Path}.
     */
    public static void setDirectory(Path directory) {
        ReadAndWriteObjectFromFile.directory = directory;
    }
}

package com.flaremars.processor.db;

import com.flaremars.annotation.Column;
import com.flaremars.annotation.Entity;
import com.flaremars.processor.ProcessingException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * Created by FlareMars on 2016/10/21.
 * 储存Storage类的基本信息
 */
public class StorageClass {

    private static final String SUFFIX = "BaseStorage";

    private TypeElement annotatedClassElement;

    private String objectClassFullName; // 实体类的全名
    private String objectClassName; // 实体类的名字

    private HashMap<String, String> requiredFields = new HashMap<>();
    private HashMap<String, String> updatedFields = new HashMap<>();
    private HashMap<String, String> findByFields = new HashMap<>();

    public StorageClass(Elements elementUtils, TypeElement classElement) throws ProcessingException {
        this.annotatedClassElement = classElement;
        Entity entityAnnotation = annotatedClassElement.getAnnotation(Entity.class);
        if (entityAnnotation.name() == null) {
            throw new ProcessingException(classElement, "name is null for %s", classElement.getQualifiedName().toString());
        }

        try {
            Class<?> objectClass = entityAnnotation.objectClass();
            objectClassFullName = objectClass.getCanonicalName();
            objectClassName = objectClass.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            objectClassFullName = classTypeElement.getQualifiedName().toString();
            objectClassName = classTypeElement.getSimpleName().toString();
        }

        List<? extends Element> allMembers = elementUtils.getAllMembers(annotatedClassElement);
        List<VariableElement> fields = ElementFilter.fieldsIn(allMembers);

        for (VariableElement field : fields) {
            Column columnProperties = field.getAnnotation(Column.class);
            if (columnProperties == null) {
                continue;
            }

            String name = field.getSimpleName().toString();
            String type = field.asType().toString();
            boolean required = columnProperties.required();
            boolean updated = columnProperties.updated();
            boolean findBy = columnProperties.findBy();

            if (required) {
                if (!name.equals("id")) {
                    requiredFields.put(name, type);
                }
            }
            if (updated) {
                updatedFields.put(name, type);
            }
            if (findBy) {
                findByFields.put(name, type);
            }
        }

    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        String storageClassName = objectClassName + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(annotatedClassElement);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString() + ".storage";

        TypeSpec.Builder classBuilder = writeSingleInstance(storageClassName);
        classBuilder.addMethod(saveMethod());
        classBuilder.addMethod(updateMethod());
        classBuilder.addMethod(saveOrUpdateMethod());
        deleteMethods(classBuilder);
        findByMethods(classBuilder);
        JavaFile.builder(packageName, classBuilder.build()).build().writeTo(filer);
    }

    // create single instance class builder
    private TypeSpec.Builder writeSingleInstance(String className) {
        MethodSpec privateConstructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build();
        MethodSpec getInstanceMethod = MethodSpec.methodBuilder("getInstance").
                addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                returns(ClassName.get("", className)).
                beginControlFlow("if (instance == null)").
                beginControlFlow("synchronized(" + className + ".class)").
                beginControlFlow("if (instance == null)").
                addStatement("instance = new " + className + "()").
                endControlFlow().
                endControlFlow().
                endControlFlow().
                addStatement("return instance").
                build();

        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(className).
                addModifiers(Modifier.PUBLIC).
                addField(ClassName.get("", className), "instance", Modifier.PRIVATE, Modifier.STATIC).
                addMethod(privateConstructor).
                addMethod(getInstanceMethod);

        return typeSpec;
    }

    private MethodSpec saveMethod() {
        String entityName = makeFirstCharLower(objectClassName);
        MethodSpec.Builder saveMethodBuilder = MethodSpec.methodBuilder("save").
                addModifiers(Modifier.PUBLIC).
                addParameter(ClassName.get("", objectClassFullName), entityName).
                returns(TypeName.BOOLEAN);
        for (String requiredField : requiredFields.keySet()) {
            saveMethodBuilder.beginControlFlow("if (" + entityName + ".get" + makeFirstCharUpper(requiredField) + "() == null)")
                    .addStatement("return false").
                    endControlFlow();
        }
        saveMethodBuilder.addStatement("return " + entityName + ".save()");

        return saveMethodBuilder.build();
    }

    private MethodSpec updateMethod() {
        String entityName = makeFirstCharLower(objectClassName);
        MethodSpec.Builder updateMethodBuilder = MethodSpec.methodBuilder("update").
                addModifiers(Modifier.PUBLIC).
                addParameter(ClassName.get("", objectClassFullName), entityName).
                returns(TypeName.BOOLEAN);

        updateMethodBuilder.addStatement("$T cv = new ContentValues()", ClassName.get("android.content", "ContentValues"));
        for (String updatedField : updatedFields.keySet()) {
            updateMethodBuilder.addStatement("cv.put($S, $L.get$L())", updatedField, entityName, makeFirstCharUpper(updatedField));
        }
        updateMethodBuilder.addStatement("return $T.update($L.class, cv, $L.getId()) > 0",
                ClassName.get("org.litepal.crud", "DataSupport"), objectClassFullName, entityName);

        return updateMethodBuilder.build();
    }

    private MethodSpec saveOrUpdateMethod() {
        String entityName = makeFirstCharLower(objectClassName);
        MethodSpec.Builder updateMethodBuilder = MethodSpec.methodBuilder("saveOrUpdate").
                addModifiers(Modifier.PUBLIC).
                addParameter(ClassName.get("", objectClassFullName), entityName).
                returns(TypeName.BOOLEAN);

        updateMethodBuilder.beginControlFlow("if ($L.getId() != null && $L.getId() != 0L)", entityName, entityName).
                addStatement("return update($L)", entityName).
                endControlFlow().
                addStatement("return save($L)", entityName);

        return updateMethodBuilder.build();
    }

    private void deleteMethods(TypeSpec.Builder classBuilder) {
        String entityName = makeFirstCharLower(objectClassName);

        // delete method for entity parameter
        MethodSpec.Builder deleteMethodBuilder = MethodSpec.methodBuilder("delete").
                addModifiers(Modifier.PUBLIC).
                addParameter(ClassName.get("", objectClassFullName), entityName).
                returns(TypeName.BOOLEAN);

        deleteMethodBuilder.beginControlFlow("if ($L.getId() == null || $L.getId() == 0L)", entityName, entityName).
                addStatement("return false").
                endControlFlow().
                addStatement("return delete($L.getId())", entityName);

        classBuilder.addMethod(deleteMethodBuilder.build());

        // delete method for id parameter
        deleteMethodBuilder = MethodSpec.methodBuilder("delete").
                addModifiers(Modifier.PUBLIC).
                addParameter(TypeName.LONG, "id").
                returns(TypeName.BOOLEAN);

        deleteMethodBuilder.addStatement("return DataSupport.delete($L.class, id) > 0", objectClassFullName);

        classBuilder.addMethod(deleteMethodBuilder.build());
    }

    private void findByMethods(TypeSpec.Builder classBuilder) {
        for (Map.Entry<String, String> entry : findByFields.entrySet()) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder("findBy" + makeFirstCharUpper(entry.getKey())).
                    addModifiers(Modifier.PUBLIC).
                    returns(ClassName.get("", objectClassFullName)).
                    addParameter(ClassName.get("", entry.getValue()), entry.getKey());

            if (entry.getKey().equals("id")) {
                builder.addStatement("return DataSupport.find($L.class, id)", objectClassFullName);
            } else {
                builder.beginControlFlow("if ($L == null)", entry.getKey()).
                        addStatement("return null").
                        endControlFlow().
                        addStatement("$T<$L> temp = DataSupport.where($S, $L).find($L.class)",
                                ClassName.get("java.util", "List"), objectClassFullName,
                                entry.getKey() + " == ?",
                                entry.getValue().equals("String") ? entry.getKey() : entry.getKey() + ".toString()",
                                objectClassFullName).
                        beginControlFlow("if (temp.size() > 0)").
                        addStatement("return temp.get(0)").
                        endControlFlow().
                        addStatement("return null");
            }

            classBuilder.addMethod(builder.build());
        }
    }

    private String makeFirstCharLower(String source) {
        if (source == null || source.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(source);
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        return builder.toString();
    }

    private String makeFirstCharUpper(String source) {
        if (source == null || source.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(source);
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        return builder.toString();
    }

    public TypeElement getClassElement() {
        return annotatedClassElement;
    }

    public String getObjectClassName() {
        return objectClassName;
    }
}

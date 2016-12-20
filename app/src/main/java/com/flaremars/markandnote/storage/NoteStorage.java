package com.flaremars.markandnote.storage;

import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.entity.storage.NoteBaseStorage;
import com.flaremars.markandnote.util.Check;
import com.flaremars.markandnote.util.StringUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by FlareMars on 2016/12/4.
 */
public class NoteStorage extends NoteBaseStorage {

    private static NoteStorage instance;

    public NoteStorage() {

    }

    public static NoteStorage getInstance() {
        if (instance == null) {
            synchronized(NoteStorage.class) {
                if (instance == null) {
                    instance = new NoteStorage();
                }
            }
        }
        return instance;
    }

    public List<Note> findAll() {
        return DataSupport.order("putTopFlag desc, date desc").find(Note.class);
    }

    @Override
    public boolean save(Note entity) {
        if (!StringUtils.INSTANCE.isEmpty(entity.getNoteId())) {
            Note check = findByNoteId(entity.getNoteId());
            if (!Check.isNull(check)) {
                entity.setId(check.getId());
                return update(entity);
            }
        }
        return super.save(entity);
    }
}

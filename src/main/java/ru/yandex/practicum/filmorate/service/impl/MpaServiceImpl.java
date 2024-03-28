package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dal.MpaDal;

import java.util.Collection;

@Service
public class MpaServiceImpl implements MpaService {
    @Autowired
    private MpaDal mpaDao;

    @Override
    public Collection<Mpa> getMpa() {
        return mpaDao.getMpa();
    }

    @Override
    public Mpa getMpaById(Long id) {
        return mpaDao.getMpaById(id);
    }
}

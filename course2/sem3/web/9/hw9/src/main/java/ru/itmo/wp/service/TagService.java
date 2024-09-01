package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.repository.TagRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Set<Tag> addTags(String tags) {
        Set<Tag> result = new HashSet<>();
        String[] names = tags.split("\\s+");
        for (String name : names) {
            Tag tag = findByName(name);
            if (tag == null) {
                tag = new Tag(name);
            }
            result.add(tag);
        }
        return result;
    }

    public Tag findByName(String name) {
        return tagRepository.findTagByName(name);
    }
}

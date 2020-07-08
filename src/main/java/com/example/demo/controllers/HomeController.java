package com.example.demo.controllers;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.algorithm.myers.MyersDiff;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

@RestController
public class HomeController {

    @RequestMapping("/")
    public String home() throws DiffException, PatchFailedException {
        List<JsonObj> list1 = new ArrayList<>();
        list1.add(new JsonObj("key1", "value1"));
        List<JsonObj> list2 = new ArrayList<>();
        list2.add(new JsonObj("key2", "value2"));


        Patch<JsonObj> patch = DiffUtils.diff(list1, list2);
        List<JsonObj> result = DiffUtils.patch(list1, patch);

        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "~")
                .newTag(f -> "-")
                .build();

        //compute the differences for two test texts.
        List<DiffRow> rows = generator.generateDiffRows(
                Arrays.asList("This is a test senctence."),
                Arrays.asList("This is a test for diffutils."));

        System.out.println(rows.get(0).getOldLine());
        String replace = rows.get(0).getOldLine().replaceFirst("~","<s>").replaceFirst("~","</s>");
       String replace2 = replace.replaceFirst("-", " <b style='color:red'>").replaceFirst("-", "</br>");

        return replace2;
    }

    public static <T> Patch<T> diff(List<T> original, List<T> revised,
                                    BiPredicate<T, T> equalizer) throws DiffException {
        if (equalizer != null) {
            return DiffUtils.diff(original, revised,
                    new MyersDiff<>(equalizer));
        }
        return DiffUtils.diff(original, revised, new MyersDiff<>());
    }
}

class JsonObj {
    public String key;
    public String value;

    public JsonObj(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
export default {
    users: {
        1: {id: 1, login: "mike", name: "Mike Mirzayanov", admin: true},
        5: {id: 5, login: "tourist", name: "Gennady Korotkevich", admin: false},
        7: {id: 7, login: "Petr", name: "Petr Mitrichev", admin: false},
        11: {id: 11, login: "andrewzta", name: "Andrew Stankevich", admin: false},
        17: {id: 17, login: "geranazavr555", name: "Georgiy Nazarov", admin: true},
    },

    userId: null,

    posts: {
        4: {
            id: 4,
            userId: 5,
            title: "About me",
            text: "Gennady Korotkevich (Belarusian: Генадзь Караткевіч, Hienadź Karatkievič, Russian: Геннадий Короткевич; born 25 September 1994) is a Belarusian competitive programmer who has won major international competitions since the age of 11, as well as numerous national competitions. His top accomplishments include six consecutive gold medals in the International Olympiad in Informatics[1] as well as the world championship in the 2013 and 2015 International Collegiate Programming Contest World Finals. As of December 2022, Gennady is the highest-rated programmer on Codeforces,[2] CodeChef,[3] Topcoder,[4] AtCoder [jp][5] and HackerRank.[6] In January 2022, he achieved a historic rating of 3979 on Codeforces, becoming the first to break the 3900 barrier."
        },
        5: {
            id: 5,
            userId: 1,
            title: "Round 987",
            text: "This round will be rated for the participants with rating lower than 2100. It will be held on extented ACM ICPC rules. The penalty for each incorrect submission until the submission with a full solution is 10 minutes. After the end of the contest you will have 12 hours to hack any solution you want. You will have access to copy any solution and test it locally."
        },
        7: {
            id: 7,
            userId: 7,
            title: "An interactive week",
            text: "AtCoder has returned with its Grand Contest 027 during the Sep 10 - Sep 16 week (problems, results, top 5 on the left, my screencast, analysis). There was a pretty tense fight for the second place with cospleermusora getting problem B accepted with less than a minute remaining; but tourist's victory was not really in doubt as he finished all problems with 25 minutes to spare. Congratulations to both!"
        },
        6: {
            id: 6,
            userId: 1,
            title: "Mail.Ru Cup 2018 — Results of R1+R2+R3",
            text: "Here are merged results of Mail.Ru Cup 2018 Round 1, Mail.Ru Cup 2018 Round 2 and Mail.Ru Cup 2018 Round 3 according to the GP100 scores (see the announcement for the details https://codeforces.com/blog/entry/62355). Best two contests give the summary score of a participant. This table is unofficial yet. But anyway... congratulations to the winnerzzzz!"
        },
        8: {
            id: 8,
            userId: 7,
            title: "A birdie week",
            text: "TopCoder SRM 736 started the Aug 13 - Aug 19 week (problems, results, top 5 on the left, my screencast, analysis). This was the first round under the new system in which one can qualify for the last online round and even to the onsite round of TopCoder Open 2019 by placing well in all SRMs in a quarter. rng_58 has claimed the early."
        },
        9: {
            id: 9,
            userId: 1,
            title: "VK Cup 2029",
            text: "Participants are invited to achieve progress in solving an unusual problem. VK Cup teams which were advanced to the Round 2 (and didn't advance to the Round 3) will take part in VK Cup 2029 - Wild-card Round 2 officially. In addition, this round will be open to the public for unofficial participation for everybody. Registration will be open for the whole round duration."
        }
    },

    comments: {
        1: {id: 1, userId: 1, postId: 4, text: "Thanks, tourist"},
        2: {id: 2, userId: 5, postId: 4, text: "You can read more on Wikipedia"},
        3: {id: 3, userId: 1, postId: 4, text: "Great!"},
        4: {id: 4, userId: 1, postId: 5, text: "Welcome to the round."},
        5: {id: 5, userId: 7, postId: 5, text: "Can Div.1 take part unofficially?"},
        6: {id: 6, userId: 5, postId: 5, text: "I hope"},
        8: {id: 8, userId: 11, postId: 8, text: "Please, write about TopCoder SRM 737"},
        9: {id: 9, userId: 7, postId: 8, text: "I'll do"}
    }
}
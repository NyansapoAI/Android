package com.example.edward.nyansapo.presentation.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edward.nyansapo.Learning_Level
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.databinding.FragmentActivitiesBinding
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_learning_level.*


class ActivitiesFragment : Fragment(R.layout.fragment_activities) {
    companion object {
        private  const val TAG="ActivitiesFragment"
    }

    lateinit var binding: FragmentActivitiesBinding
    lateinit var adapter: ActivitiesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        binding = FragmentActivitiesBinding.bind(view)


        setUpToolbar()
        setUpTabLayout()
        setUpRecyclerView()
        setOnClickListeners()



    }

    private fun setUpRecyclerView() {
        val list = getList()


        adapter = ActivitiesAdapter {
            onActivityClicked(it)
        }
        adapter.originalList =list





        adapter.filter.filter(Learning_Level.BEGINNER.name)


        binding.recyclerview.layoutManager = LinearLayoutManager(MainActivity2.activityContext!!)
        binding.recyclerview.adapter = adapter
    }

    private fun getList(): MutableList<Activity> {
        val list = mutableListOf<Activity>()
        list.add(Activity(
                "Informal Chat",
                "all",
                "1",
                "Before each class, instructors encourage children to chat, tell stories, and feel comfortable in the classroom. This helps them to express themselves, and to practice using their oral language skills. This sets the tone for all TaRL classes – throughout the class, children speak more than instructors and actively participate",
                "1. Begin by greeting the class and then talk about something relevant to the children (for example, “On my way to school….”). Speak clearly and carefully, using proper intonation and pronunciation, and use full sentences. This part of the activity strengthens children’s listening and language comprehension skills and models the correct way of speaking to a group of people \n" +
                        "2. Ask children to think about your story and the way you told it. Guiding questions could include: " +
                        "\ta. What did you think of the story? " +
                        "\tb. How did I tell the story? What kind of voice did I use?" +
                        "\tc. Who wants to tell a story like me?\n" +
                        "3. Ask children to share similar stories, encouraging as many children as possible to share ",
                "None"
        ))
        list.add(Activity(
                "Picture Reading",
                "all",
                "2",
                "Instructors use pictures to spark class discussions. They encourage children to use complete sentences when talking, and to think creatively when discussing the picture. Instructors are careful to use pictures showing familiar scenes to help children engage. This activity often takes place at the beginning of class, to draw children in and, similar to the informal chat activity, to set the tone for the rest of the class.",
                "1. Hold up a picture and ask children to describe what they see. \n" +
                        "2. Pass the picture to the children, so they can get a closer look. \n" +
                        "3. Once a few children have said something, ask children to use the picture to create a full spoken sentence.  " +
                        "4. Try to encourage as many children as possible to participate. The activity can be extended by asking children to create a story using the picture. When introducing the activity, ask children to come up with a word to describe the picture, particularly for children at the beginner and letter levels. Once they’re comfortable with this, move on to encouraging them to use full sentences. ",
                "A picture of a scene familiar to the children"
        ))
        list.add(Activity(
                "Paragraph reading",
                "all",
                "3",
                "Every day, at each level, the whole group practices reading a simple paragraph together. Remember, regardless of their current learning level, children should have the opportunity to practice reading simple texts and following along as the text is read. Following along as texts are read helps children to strengthen their listening skills, to become familiar with the process of reading text, and models proper reading behaviour (clear and fluent reading with correct intonation and pronunciation).  ",
                "1. Stick a simple paragraph on the board or wall. \n" +
                        "2. Give each child their own booklet with the same paragraph.  \n" +
                        "3.  Ask children to listen carefully as you read the paragraph. Ask children not to repeat after you, but to simply listen and follow along on the board. Read clearly, at a steady pace, and place your finger under the words as you read to help children follow along.   " +
                        "4. Ask children: “how did I read?”. Start a short discussion, helping them to think about intonation, timing, punctuation and vocal projection (depending on the class level). This discussion helps children to recognise the importance of reading aloud.  " +
                        "5. Then, ask a few children to read the paragraph for the class, just as you did.   ",
                "A simple paragraph written on the board or a piece of chart paper; a booklet with paragraphs for each child (in some contexts, instructors create their own booklets for the class, writing them in notebooks). Note: it is important that stories are created with context in mind. Children should be introduced to reading through simple, engaging texts centring on topics that are familiar and interesting to them. "
        ))

        list.add(Activity(
                "Mind Map",
                "all",
                "4",
                "Children learn to plan and organise words and sentences through a fun activity that asks them to brainstorm and map out their ideas before forming words, paragraphs, or stories. The Mind Map activity is adjusted for each level. The activity is initially done as a whole group activity, to give children practice, after which it can be done in small groups, and individually as well.",
                "1. Ask children to brainstorm a few words2. If they need help, prompt them by asking about their favourite things \n" +
                        "2. As children shout out words, write them on the board. Acknowledge all of their ideas and allow children to express themselves freely, encouraging full participation. If children get stuck on one topic (for example: school, textbook, pen, teacher), encourage them to think outside of the box and come up with a completely different word.  \n" +
                        "3. Once you have a variety of words written on the board, ask children to pick one. Circle the word they pick and erase the others. \n " +
                        "4. Ask children to identify connecting words (for example, if the circled word is tree, children might say: green, fruit, garden, etc.), draw lines and write the connecting words around the chosen word.  ",
                "chalk; writing surface (floor or board) "
        ))



        list.add(Activity(
                "Phonetic/Syllabic chart",
                "beginner and letter",
                "5",
                "The phonetic chart helps children to begin to connect the sounds they hear with specific written letters and combinations of letters. Instructors use the chart for beginner and letter level children, who need practice recognising sounds, matching them to letters, and combining sounds to form words. Children have their own small versions of the chart, which they can take home to practice forming words. The class reads the chart for a few minutes every day. This daily exposure to the phonetic chart in a low-pressure environment helps children to tease apart the different sounds within words and to begin to recognise the written shapes representing them. ",
                "1. Try to begin by connecting the chart to something the children already know: for example, ask children for their favourite word. Encourage them to pay attention to the sounds in the word. If children choose the word “dog,” ask them to think carefully about the sounds: “d” “o” and “g.” Then, find these sounds on the chart. This helps children to connect familiar words to the less familiar symbols on the chart.  \n" +
                        "2. Begin by asking children to listen and watch you carefully. Read the phonemes on the chart. \n" +
                        "3. Ask children to read the sounds as you point and identify the phonemes in their own chart. Begin by going from left to right (for example, if reading in English). Make sure to place your finger under each sound as you read. Then, vary the order, reading from right to left, horizontally, vertically, and at random. Asking children to identify phonemes out of order makes sure that they’re actually remembering which written phonemes represent which sound, rather than remembering a sequence of sounds ", "A large phoneme chart (sometimes called a syllabic chart; in some places, TaRL instructors use chart paper to create their own chart based on a smaller printed copy they receive at training), small copies of the phoneme chart for each child.   "
        ))
        list.add(Activity(
                "Copy Writing ",
                "beginner and letter",
                "6",
                "At earlier stages, children need extensive practice holding the writing instrument and forming letters. Through copying out printed sentences and checking each other’s work, children begin to recognise correctly formed letters and words, as well as appropriate spacing and punctuation for sentences.  ",
                "1. There are many ways for children to practice their writing. At the beginner and letter levels, children need a great deal of practice holding the chalk or pen and forming the letters.  \n" +
                        "2. Have a paragraph written on the board and begin by reading it in a whole group setting. 3 \n" +
                        "3. Ask children to copy the paragraph into their own notebooks. \n ", " chalk, writing surface (board or floor), pens or pencils, and notebooks "
        ))

        list.add(Activity(
                "Kambeba Game (Basket game)",
                "beginner and letter ",
                "7",
                "The Basket Game (known as the Kambeba Game in some Zambian classrooms) is a fun way to reinforce children’s letter and phoneme recognition at the beginner and letter levels.   ",
                "2. Show children the basket (or any other container) of letters/phonemes/syllables and explain the game: “We’re going to sing a song and pass the basket around. Whoever is holding the basket when the song stops should pick a card at random, read it to the class, and show the letter to their classmates.”  \n" +
                        "2. Pass the picture to the children, so they can get a closer look. \n" +
                        "3. Begin singing the song and passing the basket around.   ",
                "A basket, bag, or other container; phonetics char"
        ))

        list.add(Activity(
                "Word-Building Games ",
                "beginner, letter, word  ",
                "8",
                "This activity helps children to recognise the individual sounds within familiar words, an important foundational skill for reading (phonological awareness).  ",
                "1. Divide the class into two groups. .”  \n" +
                        "2. Ask the first group of children to think of a word. Write their chosen word on the board.  \n" +
                        "3. Ask the second group to think of a word that begins with the ending letter or sound of the first word. Write this on the board underneath the first word. ",
                "Chalk and writing surface (board or floor)"
        ))

        list.add(Activity(
                "Title Games",
                "story",
                "9",
                "Children at the story level, who can already read fluently, can begin to strengthen their comprehension skills. In these TaRL activities, children practice drawing conclusions and making inferences ",
                "1. Write a story title on the board, but not the whole story. Make sure to use a story the children haven’t read before.  \n" +
                        "2. Read the title clearly, and then ask the children to read the title.  \n" +
                        "3. Ask the class what they think the story might be about. Encourage them to be creative and to base their guesses on the title.  ",
                " chalk and writing surface (board or floor) "
        ))

        list.add(Activity(
                "Story Writing Activities",
                "word, paragraph, story ",
                "10",
                "At the word, paragraph and story levels, children begin to write their own stories. They might begin simply by writing short sentences or paragraphs, and progress to essays. Children practice writing stories as a whole group, take turns to write sentences in small groups, or practice individually. At the paragraph and story levels, children regularly practice writing on their own. Instructors might provide a topic or title or ask children to think of their own topics. Learners often read and discuss their stories, considering whether the story makes sense, and whether sentences are grammatical correct. Instructors provide one-on-one attention to learners during individual writing activities, helping them with grammar, punctuation, and spelling, and encouraging their creativity.  ",
                "1. Divide the class into groups and ask each group to appoint a leader.  \n" +
                        "2. Ask each group leader to create a sentence and write it on the floor to begin the story.  \n" +
                        "3. Remind children of these important points for story-writing before they begin:\n\t a. Make sure all the sentences connect to create a story that makes sense.\n" +
                        "\t b. The last few sentences should provide a clear end to the story. \n" +
                        "\t c. Make sure the whole story is in the same tense (if the first sentence is in past tense, make sure the story is consistently in past tense) ",
                "Chalk, writing surface (floor or other surface), notebooks, pens or pencils"
        ))

        list.add(Activity(
                "Reading Comprehension Activities",
                "paragraph and story",
                "11",
                "The Basket Game (known as the Kambeba Game in some Zambian classrooms) is a fun way to reinforce children’s letter and phoneme recognition at the beginner and letter levels.   ",
                "1. When the whole group reads a story together, begin by asking children to listen carefully as you read. Read the story in a clear voice, being careful to use proper intonation and pronunciation, and to place your finger under the words as you read. \n" +
                        "2. Then, ask a few children to read the story to the whole group, reminding them to read clearly, with proper intonation and pronunciation. \n" +
                        "3. After reading the story together, children can get into their small groups. There are a number of small group activities that children can do based on a given story:\n\t a. Mind mapping the story – help children to recall facts and events in the story, and to properly summarise the story in their own words. \n" +
                        "\tb. Question competition – each group comes up with a question about the story and the other groups have to answer. Ask children to come up with questions about facts in the story; questions that require inferring the meaning of difficult words; finding synonyms or antonyms for particular words, etc. ",
                "a story; chalk; writing surface (floor or board); notebooks; pens or pencils "
        ))












        return list


    }

    private fun onActivityClicked(it: Activity) {
        Log.d(TAG, "onActivityClicked: it:$it")
        val bundle = bundleOf("activity" to it)
        val fragment = IndividualActivitiesFragment()
        fragment.arguments = bundle
        MainActivity2.activityContext!!.supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit()

    }

    private fun setOnClickListeners() {
        binding.fob.setOnClickListener {
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.root.inflateMenu(R.menu.search_menu)
        binding.toolbar.root.setTitle("Activities")
        binding.toolbar.root.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.addStudentItem -> {
                   //
                }
            }


            true
        }
    }
    private fun setUpTabLayout() {
        //   binding.tabs.addTab(binding.tabs.newTab().setText("UNKNOWN"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Beginner"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Letter"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Word"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Paragraph"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Story"))
      //  binding.tabs.addTab(binding.tabs.newTab().setText("Above"))


        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                Log.d(TAG, "onTabSelected: $position")
                thisTabPositionHasBeenSelected(position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }

    private fun thisTabPositionHasBeenSelected(position: Int) {
        when (position) {

            0 -> {
                initRecyclerViewAdapter(Learning_Level.BEGINNER.name)
            }
            1 -> {
                initRecyclerViewAdapter(Learning_Level.LETTER.name)
            }
            2 -> {
                initRecyclerViewAdapter(Learning_Level.WORD.name)
            }
            3 -> {
                initRecyclerViewAdapter(Learning_Level.PARAGRAPH.name)
            }
            4 -> {
                initRecyclerViewAdapter(Learning_Level.STORY.name)
            }
            5 -> {
                initRecyclerViewAdapter(Learning_Level.ABOVE.name)
            }
        }


    }

    private fun initRecyclerViewAdapter(learninglevel: String) {

        adapter.filter.filter(learninglevel.toLowerCase())
    }

}
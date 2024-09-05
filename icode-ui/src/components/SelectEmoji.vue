// 选择表情组件
<script>
export default {
  name: '',
  components: {},
  data() {
    return {
      visible: false,
      emojisArr: Object.freeze([
        // 2021新版表情暂不支持
        // {emoji:'🫠',descEn:' melting face',},
        // {emoji:'🫢',descEn:' face with open eyes and hand over mouth',},
        // {emoji:'🫣',descEn:' face with peeking eye',},
        // {emoji:'🫡',descEn:' saluting face',},
        // {emoji:'🫥',descEn:' dotted line face',},
        // {emoji:'🫤',descEn:' face with diagonal mouth',},
        // {emoji:'🥹',descEn:' face holding back tears',},
        // {emoji:'🫱', descEn:'rightwards hand'},
        // {emoji:'🫲', descEn:'leftwards hand'},
        // {emoji:'🫳', descEn:'palm down hand'},
        // {emoji:'🫴', descEn:'palm up hand'},
        // {emoji:'🫰', descEn:'hand with index finger and thumb crossed'},
        // {emoji:'🫵', descEn:'index pointing at the viewer'},
        // {emoji:'🫶', descEn:'heart hands'},
        // {emoji:'🫦', descEn:'biting lip'},
        // {emoji:'🫅', descEn:'person with crown'},
        // 中文为网络翻译，不够准确，待优化
        {
          emoji: '😀',
          descEn: ' grinning face',
          descZh: '咧着嘴笑的脸',
        },
        {
          emoji: '😃',
          descEn: ' grinning face with big eyes',
          descZh: '咧着嘴笑的大眼睛',
        },
        {
          emoji: '😄',
          descEn: ' grinning face with smiling eyes',
          descZh: '微笑的脸和微笑的眼睛',
        },
        {
          emoji: '😁',
          descEn: ' beaming face with smiling eyes',
          descZh: '微笑的脸和微笑的眼睛',
        },
        {
          emoji: '😆',
          descEn: ' grinning squinting face',
          descZh: '咧着嘴眯着眼的脸',
        },
        {
          emoji: '😅',
          descEn: ' grinning face with sweat',
          descZh: '笑得满头大汗',
        },
        {
          emoji: '🤣',
          descEn: ' rolling on the floor laughing',
          descZh: '在地板上打滚大笑',
        },
        {
          emoji: '😂',
          descEn: ' face with tears of joy',
          descZh: '脸上洋溢着喜悦的泪水',
        },
        {
          emoji: '🙂',
          descEn: ' slightly smiling face',
          descZh: '微微微笑的脸',
        },
        {
          emoji: '🙃',
          descEn: ' upside-down face',
          descZh: '乱七八糟的脸',
        },
        {
          emoji: '😉',
          descEn: ' winking face',
          descZh: '眨眼的脸',
        },
        {
          emoji: '😊',
          descEn: ' smiling face with smiling eyes',
          descZh: '微笑的脸，微笑的眼睛',
        },
        {
          emoji: '😇',
          descEn: ' smiling face with halo',
          descZh: '带着光环的笑脸',
        },
        {
          emoji: '🥰',
          descEn: ' smiling face with hearts',
          descZh: '用心微笑',
        },
        {
          emoji: '😍',
          descEn: ' smiling face with heart-eyes',
          descZh: '笑脸，心眼',
        },
        {
          emoji: '🤩',
          descEn: ' star-struck',
          descZh: '随风而去',
        },
        {
          emoji: '😘',
          descEn: ' face blowing a kiss',
          descZh: '飞吻的脸',
        },
        {
          emoji: '😗',
          descEn: ' kissing face',
          descZh: '亲吻的脸',
        },
        {
          emoji: '😚',
          descEn: ' kissing face with closed eyes',
          descZh: '闭着眼睛接吻',
        },
        {
          emoji: '😙',
          descEn: ' kissing face with smiling eyes',
          descZh: '用微笑的眼睛亲吻',
        },
        {
          emoji: '🥲',
          descEn: ' smiling face with tear',
          descZh: '含泪的笑脸',
        },
        {
          emoji: '😋',
          descEn: ' face savoring food',
          descZh: '品尝食物的脸',
        },
        {
          emoji: '😛',
          descEn: ' face with tongue',
          descZh: '用舌头面对',
        },
        {
          emoji: '😜',
          descEn: ' winking face with tongue',
          descZh: '用舌头眨眼',
        },
        {
          emoji: '🤪',
          descEn: ' zany face',
          descZh: '小丑的脸',
        },
        {
          emoji: '😝',
          descEn: ' squinting face with tongue',
          descZh: '用舌头眯着脸',
        },
        {
          emoji: '🤑',
          descEn: ' money-mouth face',
          descZh: 'money-mouth脸',
        },
        {
          emoji: '🤗',
          descEn: ' smiling face with open hands',
          descZh: '张开双手微笑',
        },
        {
          emoji: '🤭',
          descEn: ' face with hand over mouth',
          descZh: '用手捂住嘴',
        },
        {
          emoji: '🤫',
          descEn: ' shushing face',
          descZh: '使安静的脸',
        },
        {
          emoji: '🤔',
          descEn: ' thinking face',
          descZh: '思维的脸',
        },
        {
          emoji: '🤐',
          descEn: ' zipper-mouth face',
          descZh: 'zipper-mouth脸',
        },
        {
          emoji: '🤨',
          descEn: ' face with raised eyebrow',
          descZh: '抬眉的脸',
        },
        {
          emoji: '😐',
          descEn: ' neutral face',
          descZh: '中性的脸',
        },
        {
          emoji: '😑',
          descEn: ' expressionless face',
          descZh: '面无表情的脸',
        },
        {
          emoji: '😶',
          descEn: ' face without mouth',
          descZh: '没有嘴的脸',
        },
        {
          emoji: '😶',
          descEn: ' face in clouds',
          descZh: '云中的脸',
        },
        {
          emoji: '😏',
          descEn: ' smirking face',
          descZh: '傻笑的脸',
        },
        {
          emoji: '😒',
          descEn: ' unamused face',
          descZh: '非娱乐性的脸',
        },
        {
          emoji: '🙄',
          descEn: ' face with rolling eyes',
          descZh: '翻白眼',
        },
        {
          emoji: '😬',
          descEn: ' grimacing face',
          descZh: '扮鬼脸的脸',
        },
        {
          emoji: '😮',
          descEn: ' face exhaling',
          descZh: '面对呼气',
        },
        {
          emoji: '🤥',
          descEn: ' lying face',
          descZh: '说谎的脸',
        },
        {
          emoji: '😌',
          descEn: ' relieved face',
          descZh: '松了一口气的脸',
        },
        {
          emoji: '😔',
          descEn: ' pensive face',
          descZh: '忧郁的脸',
        },
        {
          emoji: '😪',
          descEn: ' sleepy face',
          descZh: '沉睡的脸',
        },
        {
          emoji: '🤤',
          descEn: ' drooling face',
          descZh: '流口水的脸',
        },
        {
          emoji: '😴',
          descEn: ' sleeping face',
          descZh: '熟睡的脸',
        },
        {
          emoji: '😷',
          descEn: ' face with medical mask',
          descZh: '戴医用口罩',
        },
        {
          emoji: '🤒',
          descEn: ' face with thermometer',
          descZh: '带温度计的脸',
        },
        {
          emoji: '🤕',
          descEn: ' face with head-bandage',
          descZh: '带头巾的脸',
        },
        {
          emoji: '🤢',
          descEn: ' nauseated face',
          descZh: '恶心的脸',
        },
        {
          emoji: '🤮',
          descEn: ' face vomiting',
          descZh: '面对呕吐',
        },
        {
          emoji: '🤧',
          descEn: ' sneezing face',
          descZh: '打喷嚏的脸',
        },
        {
          emoji: '🥵',
          descEn: ' hot face',
          descZh: '热脸',
        },
        {
          emoji: '🥶',
          descEn: ' cold face',
          descZh: '冷脸',
        },
        {
          emoji: '🥴',
          descEn: ' woozy face',
          descZh: '微醉的脸',
        },
        {
          emoji: '😵',
          descEn: ' face with crossed-out eyes',
          descZh: '眼睛被划掉的脸',
        },
        {
          emoji: '😵',
          descEn: ' face with spiral eyes',
          descZh: '螺旋形眼睛的脸',
        },
        {
          emoji: '🤯',
          descEn: ' exploding head',
          descZh: '爆炸头',
        },
        {
          emoji: '🤠',
          descEn: ' cowboy hat face',
          descZh: '牛仔帽脸',
        },
        {
          emoji: '🥳',
          descEn: ' partying face',
          descZh: '聚会的脸',
        },
        {
          emoji: '🥸',
          descEn: ' disguised face',
          descZh: '伪装的脸',
        },
        {
          emoji: '😎',
          descEn: ' smiling face with sunglasses',
          descZh: '戴墨镜的笑脸',
        },
        {
          emoji: '🤓',
          descEn: ' nerd face',
          descZh: '书呆子的脸',
        },
        {
          emoji: '🧐',
          descEn: ' face with monocle',
          descZh: '戴单片眼镜',
        },
        {
          emoji: '😕',
          descEn: ' confused face',
          descZh: '困惑的脸',
        },
        {
          emoji: '😟',
          descEn: ' worried face',
          descZh: '担心的脸',
        },
        {
          emoji: '🙁',
          descEn: ' slightly frowning face',
          descZh: '微皱的脸',
        },
        {
          emoji: '😮',
          descEn: ' face with open mouth',
          descZh: '张大嘴巴',
        },
        {
          emoji: '😯',
          descEn: ' hushed face',
          descZh: '安静的脸',
        },
        {
          emoji: '😲',
          descEn: ' astonished face',
          descZh: '惊讶的脸',
        },
        {
          emoji: '😳',
          descEn: ' flushed face',
          descZh: '泛红的脸',
        },
        {
          emoji: '🥺',
          descEn: ' pleading face',
          descZh: '恳求的脸',
        },
        {
          emoji: '😦',
          descEn: ' frowning face with open mouth',
          descZh: '皱着眉头，张着嘴',
        },
        {
          emoji: '😧',
          descEn: ' anguished face',
          descZh: '痛苦的脸',
        },
        {
          emoji: '😨',
          descEn: ' fearful face',
          descZh: '可怕的脸',
        },
        {
          emoji: '😰',
          descEn: ' anxious face with sweat',
          descZh: '汗流浃背的焦虑脸',
        },
        {
          emoji: '😥',
          descEn: ' sad but relieved face',
          descZh: '悲伤但释然的脸',
        },
        {
          emoji: '😢',
          descEn: ' crying face',
          descZh: '哭泣的脸',
        },
        {
          emoji: '😭',
          descEn: ' loudly crying face',
          descZh: '大声哭泣的脸',
        },
        {
          emoji: '😱',
          descEn: ' face screaming in fear',
          descZh: '面对恐惧尖叫',
        },
        {
          emoji: '😖',
          descEn: ' confounded face',
          descZh: '蒙羞的脸',
        },
        {
          emoji: '😣',
          descEn: ' persevering face',
          descZh: '坚持的脸',
        },
        {
          emoji: '😞',
          descEn: ' disappointed face',
          descZh: '失望的脸',
        },
        {
          emoji: '😓',
          descEn: ' downcast face with sweat',
          descZh: '垂头丧气，满头大汗',
        },
        {
          emoji: '😩',
          descEn: ' weary face',
          descZh: '疲惫的脸',
        },
        {
          emoji: '😫',
          descEn: ' tired face',
          descZh: '疲惫的脸',
        },
        {
          emoji: '🥱',
          descEn: ' yawning face',
          descZh: '打呵欠的脸',
        },
        {
          emoji: '😤',
          descEn: ' face with steam from nose',
          descZh: '脸上有蒸汽从鼻子',
        },
        {
          emoji: '😡',
          descEn: ' enraged face',
          descZh: '愤怒的脸',
        },
        {
          emoji: '😠',
          descEn: ' angry face',
          descZh: '愤怒的脸',
        },
        {
          emoji: '🤬',
          descEn: ' face with symbols on mouth',
          descZh: '脸上有符号的嘴',
        },
        {
          emoji: '😈',
          descEn: ' smiling face with horns',
          descZh: '长着角的笑脸',
        },
        {
          emoji: '👿',
          descEn: ' angry face with horns',
          descZh: '长角的愤怒脸',
        },
        {
          emoji: '💀',
          descEn: ' skull',
          descZh: '头骨',
        },
        {
          emoji: '☠️',
          descEn: ' skull and crossbones',
          descZh: '头骨和交叉骨',
        },
        {
          emoji: '💩',
          descEn: ' pile of poo',
          descZh: '一堆便便',
        },
        {
          emoji: '🤡',
          descEn: ' clown face',
          descZh: '小丑的脸',
        },
        {
          emoji: '👹',
          descEn: ' ogre',
          descZh: '食人魔',
        },
        {
          emoji: '👺',
          descEn: ' goblin',
          descZh: '小妖精',
        },
        {
          emoji: '👻',
          descEn: ' ghost',
          descZh: '鬼',
        },
        {
          emoji: '👽',
          descEn: ' alien',
          descZh: '外星人',
        },
        {
          emoji: '👾',
          descEn: ' alien monster',
          descZh: '外星怪物',
        },
        {
          emoji: '🤖',
          descEn: ' robot',
          descZh: '机器人',
        },
        {
          emoji: '😺',
          descEn: ' grinning cat',
          descZh: '笑猫',
        },
        {
          emoji: '😸',
          descEn: ' grinning cat with smiling eyes',
          descZh: '笑眯眯的猫',
        },
        {
          emoji: '😹',
          descEn: ' cat with tears of joy',
          descZh: '猫带着喜悦的泪水',
        },
        {
          emoji: '😻',
          descEn: ' smiling cat with heart-eyes',
          descZh: '带着心形眼睛的微笑猫',
        },
        {
          emoji: '😼',
          descEn: ' cat with wry smile',
          descZh: '苦笑的猫',
        },
        {
          emoji: '😽',
          descEn: ' kissing cat',
          descZh: '接吻猫',
        },
        {
          emoji: '🙀',
          descEn: ' weary cat',
          descZh: '疲倦的猫',
        },
        {
          emoji: '😿',
          descEn: ' crying cat',
          descZh: '哭的猫',
        },
        {
          emoji: '😾',
          descEn: ' pouting cat',
          descZh: '撅嘴的猫',
        },
        {
          emoji: '🙈',
          descEn: ' see-no-evil monkey',
          descZh: '非礼勿视的猴子',
        },
        {
          emoji: '🙉',
          descEn: ' hear-no-evil monkey',
          descZh: '猴子不掉泪',
        },
        {
          emoji: '🙊',
          descEn: ' speak-no-evil monkey',
          descZh: 'speak-no-evil猴子',
        },
        {
          emoji: '💌',
          descEn: ' love letter',
          descZh: '情书',
        },
        {
          emoji: '💘',
          descEn: ' heart with arrow',
          descZh: '心带箭',
        },
        {
          emoji: '💝',
          descEn: ' heart with ribbon',
          descZh: '带丝带的心',
        },
        {
          emoji: '💖',
          descEn: ' sparkling heart',
          descZh: '闪闪发光的心',
        },
        {
          emoji: '💗',
          descEn: ' growing heart',
          descZh: '成长的心',
        },
        {
          emoji: '💓',
          descEn: ' beating heart',
          descZh: '跳动的心脏',
        },
        {
          emoji: '💞',
          descEn: ' revolving hearts',
          descZh: '旋转的心',
        },
        {
          emoji: '💕',
          descEn: ' two hearts',
          descZh: '两颗心',
        },
        {
          emoji: '💟',
          descEn: ' heart decoration',
          descZh: '心装饰',
        },
        {
          emoji: '❣️',
          descEn: ' heart exclamation',
          descZh: '心感叹',
        },
        {
          emoji: '💔',
          descEn: ' broken heart',
          descZh: '破碎的心',
        },
        {
          emoji: '❤️',
          descEn: ' heart on fire',
          descZh: '心如烈火',
        },
        {
          emoji: '❤️',
          descEn: ' mending heart',
          descZh: '修补心脏',
        },
        {
          emoji: '❤️',
          descEn: ' red heart',
          descZh: '红色的心',
        },
        {
          emoji: '🧡',
          descEn: ' orange heart',
          descZh: '橙色的心',
        },
        {
          emoji: '💛',
          descEn: ' yellow heart',
          descZh: '黄色的心',
        },
        {
          emoji: '💚',
          descEn: ' green heart',
          descZh: '绿色的心',
        },
        {
          emoji: '💙',
          descEn: ' blue heart',
          descZh: '蓝色的心',
        },
        {
          emoji: '💜',
          descEn: ' purple heart',
          descZh: '紫心勋章',
        },
        {
          emoji: '🤎',
          descEn: ' brown heart',
          descZh: '布朗的心',
        },
        {
          emoji: '🖤',
          descEn: ' black heart',
          descZh: '黑色的心',
        },
        {
          emoji: '🤍',
          descEn: ' white heart',
          descZh: '白色的心',
        },
        {
          emoji: '💋',
          descEn: ' kiss mark',
          descZh: '吻马克',
        },
        {
          emoji: '💯',
          descEn: ' hundred points',
          descZh: '几百点',
        },
        {
          emoji: '💢',
          descEn: ' anger symbol',
          descZh: '愤怒的象征',
        },
        {
          emoji: '💥',
          descEn: ' collision',
          descZh: '碰撞',
        },
        {
          emoji: '💫',
          descEn: ' dizzy',
          descZh: '头晕',
        },
        {
          emoji: '💦',
          descEn: ' sweat droplets',
          descZh: '汗水滴',
        },
        {
          emoji: '💨',
          descEn: ' dashing away',
          descZh: '潇洒了',
        },
        {
          emoji: '💬',
          descEn: ' speech balloon',
          descZh: '演讲的气球',
        },
        {
          emoji: '💭',
          descEn: ' thought balloon',
          descZh: '认为气球',
        },
        {
          emoji: '💤',
          descEn: ' ZZZ',
          descZh: '打鼾声',
        },
        {
          emoji: '👋',
          descEn: 'waving hand',
          descZh: '挥舞的手',
        },
        {
          emoji: '🤚',
          descEn: 'raised back of hand',
          descZh: '举起手背',
        },
        {
          emoji: '✋',
          descEn: 'raised hand',
          descZh: '举起手',
        },
        {
          emoji: '🖖',
          descEn: 'vulcan salute',
          descZh: '火神致敬',
        },
        {
          emoji: '👌',
          descEn: 'OK hand',
          descZh: '好的手',
        },
        {
          emoji: '🤌',
          descEn: 'pinched fingers',
          descZh: '捏手指',
        },
        {
          emoji: '🤏',
          descEn: 'pinching hand',
          descZh: '捏手',
        },
        {
          emoji: '🤞',
          descEn: 'crossed fingers',
          descZh: '交叉手指',
        },
        {
          emoji: '🤟',
          descEn: 'love-you gesture',
          descZh: '怀抱之中,姿态',
        },
        {
          emoji: '🤘',
          descEn: 'sign of the horns',
          descZh: '角的标志',
        },
        {
          emoji: '🤙',
          descEn: 'call me hand',
          descZh: '呼唤我的手',
        },
        {
          emoji: '👈',
          descEn: 'backhand index pointing left',
          descZh: '反手索引指向左边',
        },
        {
          emoji: '👉',
          descEn: 'backhand index pointing right',
          descZh: '反手索引指向右',
        },
        {
          emoji: '👆',
          descEn: 'backhand index pointing up',
          descZh: '反手指数向上',
        },
        {
          emoji: '🖕',
          descEn: 'middle finger',
          descZh: '中指',
        },
        {
          emoji: '👇',
          descEn: 'backhand index pointing down',
          descZh: '反手索引指向下方',
        },
        {
          emoji: '👍',
          descEn: 'thumbs up',
          descZh: '竖起大拇指',
        },
        {
          emoji: '👎',
          descEn: 'thumbs down',
          descZh: '拇指向下',
        },
        {
          emoji: '✊',
          descEn: 'raised fist',
          descZh: '高举的拳头',
        },
        {
          emoji: '👊',
          descEn: 'oncoming fist',
          descZh: '迎面而来的拳头',
        },
        {
          emoji: '🤛',
          descEn: 'left-facing fist',
          descZh: '左转的拳头',
        },
        {
          emoji: '🤜',
          descEn: 'right-facing fist',
          descZh: 'right-facing拳头',
        },
        {
          emoji: '👏',
          descEn: 'clapping hands',
          descZh: '鼓掌的手',
        },
        {
          emoji: '🙌',
          descEn: 'raising hands',
          descZh: '提高手',
        },
        {
          emoji: '👐',
          descEn: 'open hands',
          descZh: '打开手',
        },
        {
          emoji: '🤲',
          descEn: 'palms up together',
          descZh: '手掌朝上',
        },
        {
          emoji: '🤝',
          descEn: 'handshake',
          descZh: '握手',
        },
        {
          emoji: '🙏',
          descEn: 'folded hands',
          descZh: '折叠的手',
        },
        {
          emoji: '💅',
          descEn: 'nail polish',
          descZh: '指甲油',
        },
        {
          emoji: '🤳',
          descEn: 'selfie',
          descZh: '有问题',
        },
        {
          emoji: '💪',
          descEn: 'flexed biceps',
          descZh: '弯曲的二头肌',
        },
        {
          emoji: '🦾',
          descEn: 'mechanical arm',
          descZh: '机械臂',
        },
        {
          emoji: '🦿',
          descEn: 'mechanical leg',
          descZh: '机械腿',
        },
        {
          emoji: '🦵',
          descEn: 'leg',
          descZh: '腿',
        },
        {
          emoji: '🦶',
          descEn: 'foot',
          descZh: '脚',
        },
        {
          emoji: '👂',
          descEn: 'ear',
          descZh: '耳朵',
        },
        {
          emoji: '🦻',
          descEn: 'ear with hearing aid',
          descZh: '戴助听器的耳朵',
        },
        {
          emoji: '👃',
          descEn: 'nose',
          descZh: '鼻子',
        },
        {
          emoji: '🧠',
          descEn: 'brain',
          descZh: '大脑',
        },
        {
          emoji: '🫀',
          descEn: 'anatomical heart',
          descZh: '解剖的心',
        },
        {
          emoji: '🫁',
          descEn: 'lungs',
          descZh: '肺',
        },
        {
          emoji: '🦷',
          descEn: 'tooth',
          descZh: '牙',
        },
        {
          emoji: '🦴',
          descEn: 'bone',
          descZh: '骨',
        },
        {
          emoji: '👀',
          descEn: 'eyes',
          descZh: '眼睛',
        },
        {
          emoji: '👅',
          descEn: 'tongue',
          descZh: '舌头',
        },
        {
          emoji: '👄',
          descEn: 'mouth',
          descZh: '口',
        },
        {
          emoji: '👶',
          descEn: 'baby',
          descZh: '婴儿',
        },
        {
          emoji: '🧒',
          descEn: 'child',
          descZh: '孩子',
        },
        {
          emoji: '👦',
          descEn: 'boy',
          descZh: '男孩',
        },
        {
          emoji: '👧',
          descEn: 'girl',
          descZh: '女孩',
        },
        {
          emoji: '🧑',
          descEn: 'person',
          descZh: '人',
        },
        {
          emoji: '👱',
          descEn: 'person: blond hair',
          descZh: '人:金发',
        },
        {
          emoji: '👨',
          descEn: 'man',
          descZh: '男人。',
        },
        {
          emoji: '🧔',
          descEn: 'person: beard',
          descZh: '人:胡子',
        },
        {
          emoji: '👩',
          descEn: 'woman',
          descZh: '女人',
        },
        {
          emoji: '🧓',
          descEn: 'older person',
          descZh: '年长的人',
        },
        {
          emoji: '👴',
          descEn: 'old man',
          descZh: '老人',
        },
        {
          emoji: '👵',
          descEn: 'old woman',
          descZh: '老女人',
        },
        {
          emoji: '🙍',
          descEn: 'person frowning',
          descZh: '人皱着眉头',
        },
        {
          emoji: '🙎',
          descEn: 'person pouting',
          descZh: '人撅嘴',
        },
        {
          emoji: '🙅',
          descEn: 'person gesturing NO',
          descZh: '手势“不”的人',
        },
        {
          emoji: '🙆',
          descEn: 'person gesturing OK',
          descZh: '手势OK的人',
        },
        {
          emoji: '💁',
          descEn: 'person tipping hand',
          descZh: '给小费的人',
        },
        {
          emoji: '🙋',
          descEn: 'person raising hand',
          descZh: '举手的人',
        },
        {
          emoji: '🧏',
          descEn: 'deaf person',
          descZh: '失聪的人',
        },
        {
          emoji: '🙇',
          descEn: 'person bowing',
          descZh: '人鞠躬',
        },
        {
          emoji: '🤦',
          descEn: 'person facepalming',
          descZh: '人捂脸',
        },
        {
          emoji: '🤷',
          descEn: 'person shrugging',
          descZh: '人耸',
        },
        {
          emoji: '👮',
          descEn: 'police officer',
          descZh: '警察',
        },
        {
          emoji: '💂',
          descEn: 'guard',
          descZh: '警卫',
        },
        {
          emoji: '🥷',
          descEn: 'ninja',
          descZh: '忍者',
        },
        {
          emoji: '👷',
          descEn: 'construction worker',
          descZh: '建筑工人',
        },
        {
          emoji: '🤴',
          descEn: 'prince',
          descZh: '王子',
        },
        {
          emoji: '👸',
          descEn: 'princess',
          descZh: '公主',
        },
        {
          emoji: '👳',
          descEn: 'person wearing turban',
          descZh: '戴头巾者',
        },
        {
          emoji: '👲',
          descEn: 'person with skullcap',
          descZh: '戴无檐便帽的人',
        },
        {
          emoji: '🧕',
          descEn: 'woman with headscarf',
          descZh: '戴头巾的妇女',
        },
        {
          emoji: '🤵',
          descEn: 'person in tuxedo',
          descZh: '穿燕尾服的人',
        },
        {
          emoji: '👰',
          descEn: 'person with veil',
          descZh: '戴面纱者',
        },
      ]),
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    select(emoji) {
      this.visible = false
      this.$emit('select', emoji)
    },
  },
}
</script>

<template>
  <div class="copo-emoji" title="插入表情">
    <el-popover placement="right" trigger="click" v-model:visible="visible">
      <ul class="emoji-wrap">
        <li
          v-for="(item, index) in emojisArr"
          :key="index"
          class="emoji"
          :title="item.descZh"
          @click="select(item.emoji)">
          {{ item.emoji }}
        </li>
      </ul>
      <template #reference>
        <div>
          <slot v-bind:emojisArr="emojisArr">
            {{ emojisArr[0].emoji }}
          </slot>
        </div>
      </template>
    </el-popover>
  </div>
</template>

<style lang="scss" scoped>
.copo-emoji {
  display: inline-block;
}
.emoji-wrap {
  display: inline-block;
  width: 400px;
  height: 400px;
  overflow: auto;
  .emoji {
    display: inline-block;
    padding: 7px 7px 4px;
    border-radius: 4px;
    margin: 3px;
    cursor: pointer;
    &:hover {
      background: var(--bg-black-9);
    }
  }
}
</style>

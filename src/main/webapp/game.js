// 单词库 - 100+ 个日常生活常见单词
const wordLibrary = [
    'apple', 'banana', 'orange', 'grape', 'pear', 'peach', 'watermelon', 'strawberry', 'blueberry', 'raspberry',
    'cat', 'dog', 'bird', 'fish', 'rabbit', 'hamster', 'guinea', 'pig', 'cow', 'horse',
    'car', 'bus', 'train', 'plane', 'ship', 'bike', 'motorcycle', 'truck', 'van', 'taxi',
    'book', 'pen', 'pencil', 'paper', 'notebook', 'eraser', 'ruler', 'scissors', 'glue', 'stapler',
    'house', 'apartment', 'building', 'room', 'kitchen', 'bathroom', 'bedroom', 'living', 'dining', 'garage',
    'computer', 'phone', 'tablet', 'laptop', 'keyboard', 'mouse', 'monitor', 'printer', 'scanner', 'camera',
    'sun', 'moon', 'star', 'cloud', 'rain', 'snow', 'wind', 'storm', 'thunder', 'lightning',
    'tree', 'flower', 'grass', 'leaf', 'root', 'branch', 'trunk', 'bark', 'fruit', 'seed',
    'food', 'drink', 'bread', 'rice', 'noodle', 'pasta', 'meat', 'chicken', 'beef', 'pork',
    'music', 'song', 'sing', 'dance', 'piano', 'guitar', 'violin', 'drum', 'flute', 'trumpet',
    'happy', 'sad', 'angry', 'excited', 'tired', 'hungry', 'thirsty', 'sleepy', 'bored', 'scared',
    'red', 'blue', 'green', 'yellow', 'orange', 'purple', 'pink', 'black', 'white', 'brown'
];

// 提示信息库 - 每个单词有多个提示
const hintLibrary = {
    'apple': [
        '一种红色或绿色的水果，乔布斯创立的公司标志',
        '富含维生素C，常被称为"一天一苹果，医生远离我"',
        '可以直接吃，也可以做成果酱或派'
    ],
    'banana': [
        '黄色的长条形水果，猴子喜欢吃',
        '富含钾元素，有助于心脏健康',
        '可以剥皮直接吃，也可以做香蕉船'
    ],
    'orange': [
        '橙色的水果，富含维生素C',
        '有厚厚的果皮，需要剥开吃',
        '可以榨汁，是常见的果汁原料'
    ],
    'grape': [
        '成串生长的小水果，可以酿酒',
        '有紫色、绿色等多种颜色',
        '可以直接吃，也可以制成葡萄干'
    ],
    'pear': [
        '形状像葫芦的水果',
        '果肉脆甜，水分很多',
        '常用来煮汤或做罐头'
    ],
    'peach': [
        '粉红色的水果，象征长寿',
        '果肉柔软多汁，味道香甜',
        '有"仙桃"的美称'
    ],
    'watermelon': [
        '夏天常见的大型水果，内部是红色的',
        '水分很多，是消暑解渴的好选择',
        '有黑色的籽，现在有无籽品种'
    ],
    'strawberry': [
        '红色的小水果，表面有小籽',
        '味道酸甜，常用来做甜点',
        '心形的外形，很受欢迎'
    ],
    'cat': [
        '常见的宠物，喜欢吃鱼和老鼠',
        '会发出"喵喵"的叫声',
        '有九条命的传说'
    ],
    'dog': [
        '人类最好的朋友，忠诚的宠物',
        '会发出"汪汪"的叫声',
        '有很多品种，如金毛、哈士奇等'
    ],
    'bird': [
        '有翅膀会飞的动物',
        '有羽毛和喙，下蛋繁殖',
        '可以唱歌，如鹦鹉、画眉等'
    ],
    'fish': [
        '生活在水中的动物，用鳃呼吸',
        '有鳞片和鳍，会游泳',
        '是常见的食物来源'
    ],
    'car': [
        '四个轮子的交通工具',
        '有发动机，可以快速行驶',
        '有轿车、SUV等多种类型'
    ],
    'bus': [
        '公共交通工具，载很多乘客',
        '有固定的路线和站点',
        '是城市交通的重要组成部分'
    ],
    'train': [
        '在铁轨上行驶的交通工具',
        '可以载很多乘客和货物',
        '有高铁、动车等不同类型'
    ],
    'plane': [
        '可以在空中飞行的交通工具',
        '有翅膀和发动机，速度很快',
        '可以长途旅行，飞越海洋'
    ],
    'book': [
        '包含文字和图片的阅读材料',
        '有封面和书页，需要装订',
        '可以增长知识，开阔眼界'
    ],
    'pen': [
        '用来写字的工具',
        '有钢笔、圆珠笔等多种类型',
        '需要墨水或笔芯才能写字'
    ],
    'house': [
        '人们居住的地方',
        '有墙壁、屋顶和门窗',
        '可以遮风挡雨，提供安全'
    ],
    'computer': [
        '现代电子设备，用于工作和娱乐',
        '有CPU、内存和硬盘等部件',
        '可以上网、办公、玩游戏'
    ],
    'phone': [
        '通讯工具，可以打电话和上网',
        '有智能手机和功能手机',
        '可以拍照、听音乐、看视频'
    ],
    'sun': [
        '太阳系的中心，提供光和热',
        '是一颗恒星，非常巨大',
        '白天出现，夜晚消失'
    ],
    'moon': [
        '地球的卫星，夜晚发光',
        '本身不发光，反射太阳光',
        '有阴晴圆缺的变化'
    ],
    'tree': [
        '高大的植物，有树干和树叶',
        '可以提供氧气，吸收二氧化碳',
        '有树根、树枝和树皮'
    ],
    'flower': [
        '植物的繁殖器官，通常很美丽',
        '有花瓣和花蕊，会开花结果',
        '有很多颜色和品种'
    ],
    'food': [
        '人们吃的东西，提供能量',
        '有主食、副食、零食等',
        '可以分为荤食和素食'
    ],
    'music': [
        '由声音组成的艺术形式',
        '有旋律和节奏，很动听',
        '可以分为古典、流行、摇滚等'
    ],
    'happy': [
        '感到快乐的情绪',
        '会微笑，心情很好',
        '是积极向上的情绪'
    ],
    'sad': [
        '感到悲伤的情绪',
        '会流泪，心情不好',
        '是消极的情绪'
    ],
    'red': [
        '像血一样的颜色',
        '是中国国旗的颜色',
        '象征热情和活力'
    ],
    'blue': [
        '像天空一样的颜色',
        '是大海的颜色',
        '象征冷静和忧郁'
    ],
    'green': [
        '像草一样的颜色',
        '是树叶的颜色',
        '象征生命和环保'
    ],
    'yellow': [
        '像太阳一样的颜色',
        '是黄金的颜色',
        '象征光明和快乐'
    ]
};

new Vue({
    el: '#app',
    data() {
        return {
            gameState: 'start', // start, playing, ended
            maxAttempts: '',
            remainingAttempts: 0,
            targetWord: '',
            displayedWord: [],
            guessedLetters: [],
            currentGuess: '',
            message: '',
            messageType: '',
            hints: [],
            availableHints: 0,
            hint: '',
            correctGuesses: 0,
            incorrectGuesses: 0
        };
    },
    methods: {
        // 开始游戏
        startGame() {
            const attempts = parseInt(this.maxAttempts);
            if (isNaN(attempts) || attempts < 5 || attempts > 20) {
                this.showMessage('请输入5-20之间的有效数字', 'error');
                return;
            }
            this.maxAttempts = attempts;
            
            // 随机选择一个单词
            this.targetWord = wordLibrary[Math.floor(Math.random() * wordLibrary.length)];
            
            // 初始化显示的单词（下划线）
            this.displayedWord = Array(this.targetWord.length).fill('_');
            
            // 重置游戏状态
            this.gameState = 'playing';
            this.remainingAttempts = this.maxAttempts;
            this.guessedLetters = [];
            this.currentGuess = '';
            this.message = '';
            this.messageType = '';
            this.hints = [];
            this.availableHints = 0;
            this.hint = '';
            this.correctGuesses = 0;
            this.incorrectGuesses = 0;
        },
        
        // 猜字母
        makeGuess() {
            const guess = this.currentGuess.toLowerCase().trim();
            
            // 验证输入
            if (!guess.match(/^[a-z]$/)) {
                this.showMessage('请输入一个有效的字母（A-Z）', 'error');
                this.currentGuess = '';
                this.$nextTick(() => {
                    this.$refs.guessInput.focus();
                });
                return;
            }
            
            // 检查是否已经猜过这个字母
            if (this.guessedLetters.includes(guess)) {
                this.showMessage('你已经猜过这个字母了', 'error');
                this.currentGuess = '';
                this.$nextTick(() => {
                    this.$refs.guessInput.focus();
                });
                return;
            }
            
            // 添加到已猜字母列表
            this.guessedLetters.push(guess);
            
            // 检查是否猜对
            if (this.targetWord.includes(guess)) {
                // 猜对了，显示所有该字母的位置
                for (let i = 0; i < this.targetWord.length; i++) {
                    if (this.targetWord[i] === guess) {
                        this.displayedWord[i] = guess;
                    }
                }
                this.correctGuesses++;
                this.showMessage('猜对了！', 'success');
                
                // 检查是否猜完所有字母
                if (!this.displayedWord.includes('_')) {
                    setTimeout(() => {
                        this.endGame(true);
                    }, 500);
                } else {
                    this.currentGuess = '';
                    this.$nextTick(() => {
                        this.$refs.guessInput.focus();
                    });
                }
            } else {
                // 猜错了
                this.remainingAttempts--;
                this.incorrectGuesses++;
                this.showMessage('猜错了！', 'error');
                
                // 每猜错三次，增加一次提示机会
                if (this.incorrectGuesses % 3 === 0) {
                    this.availableHints++;
                    this.showMessage(`恭喜！你获得了一次提示机会（共${this.availableHints}次）`, 'info');
                }
                
                // 检查是否用完所有次数
                if (this.remainingAttempts <= 0) {
                    setTimeout(() => {
                        this.endGame(false);
                    }, 500);
                } else {
                    this.currentGuess = '';
                    this.$nextTick(() => {
                        this.$refs.guessInput.focus();
                    });
                }
            }
        },
        
        // 获取提示
        getHint() {
            if (this.availableHints <= 0) {
                this.showMessage('你没有可用的提示机会，请猜错三次获得一次提示', 'error');
                return;
            }
            
            // 获取该单词的所有提示
            const allHints = hintLibrary[this.targetWord] || ['这是一个常见的英文单词'];
            
            // 过滤掉已经显示过的提示
            const unusedHints = allHints.filter(hint => !this.hints.includes(hint));
            
            // 如果所有提示都用过了，重新使用第一个提示
            const wordHint = unusedHints.length > 0 ? unusedHints[0] : allHints[0];
            
            this.hints.push(wordHint);
            this.hint = wordHint;
            this.availableHints--;
            this.showMessage('获得提示！', 'info');
        },
        
        // 结束游戏
        endGame(won) {
            this.gameState = 'ended';
            if (won) {
                this.message = `恭喜你在${this.maxAttempts - this.remainingAttempts}次猜测内猜对了单词！`;
                this.messageType = 'success';
            } else {
                this.message = `很遗憾，你用完了所有次数。正确单词是${this.targetWord}。`;
                this.messageType = 'error';
            }
        },
        
        // 重置游戏
        resetGame() {
            this.gameState = 'start';
            this.maxAttempts = 0;
            this.message = '';
            this.messageType = '';
        },
        
        // 显示消息
        showMessage(msg, type) {
            this.message = msg;
            this.messageType = type;
            
            // 3秒后自动清除消息
            setTimeout(() => {
                this.message = '';
                this.messageType = '';
            }, 3000);
        }
    }
});
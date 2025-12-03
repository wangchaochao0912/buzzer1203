new Vue({
    el: '#app',
    data: {
        gameState: 'setup', // setup, playing, won, lost
        maxAttempts: 10,
        remainingAttempts: 10,
        targetWord: '',
        displayWord: [],
        correctGuesses: [],
        wrongGuesses: [],
        currentGuess: '',
        hints: [],
        wordDatabase: [
            // 日常生活用品
            'apple', 'banana', 'orange', 'grape', 'melon',
            'bread', 'cake', 'pizza', 'burger', 'fries',
            'water', 'juice', 'coffee', 'tea', 'milk',
            'chair', 'table', 'sofa', 'bed', 'desk',
            'book', 'pen', 'pencil', 'paper', 'notebook',
            'phone', 'computer', 'tv', 'radio', 'camera',
            'clock', 'watch', 'mirror', 'lamp', 'light',
            'door', 'window', 'wall', 'floor', 'ceiling',
            'shoe', 'sock', 'shirt', 'pants', 'dress',
            'hat', 'glasses', 'umbrella', 'bag', 'wallet',
            
            // 动物
            'dog', 'cat', 'bird', 'fish', 'mouse',
            'horse', 'cow', 'sheep', 'pig', 'chicken',
            'duck', 'goose', 'rabbit', 'tiger', 'lion',
            'bear', 'monkey', 'elephant', 'giraffe', 'zebra',
            'deer', 'fox', 'wolf', 'cow', 'sheep',
            
            // 颜色
            'red', 'blue', 'green', 'yellow', 'orange',
            'purple', 'pink', 'brown', 'black', 'white',
            'gray', 'gold', 'silver', 'bronze', 'copper',
            
            // 数字
            'one', 'two', 'three', 'four', 'five',
            'six', 'seven', 'eight', 'nine', 'ten',
            'eleven', 'twelve', 'thirteen', 'fourteen', 'fifteen',
            
            // 月份
            'january', 'february', 'march', 'april', 'may',
            'june', 'july', 'august', 'september', 'october',
            'november', 'december',
            
            // 星期
            'monday', 'tuesday', 'wednesday', 'thursday', 'friday',
            'saturday', 'sunday',
            
            // 身体部位
            'head', 'eye', 'ear', 'nose', 'mouth',
            'hand', 'foot', 'arm', 'leg', 'finger',
            'toe', 'hair', 'face', 'neck', 'shoulder',
            
            // 交通工具
            'car', 'bus', 'train', 'plane', 'bike',
            'boat', 'ship', 'truck', 'taxi', 'bike',
            
            // 天气
            'sun', 'rain', 'snow', 'wind', 'cloud',
            'storm', 'fog', 'hail', 'thunder', 'lightning',
            
            // 其他常用词
            'home', 'school', 'work', 'park', 'store',
            'city', 'town', 'village', 'country', 'world',
            'day', 'night', 'time', 'year', 'month',
            'week', 'hour', 'minute', 'second', 'today',
            'yesterday', 'tomorrow', 'morning', 'afternoon', 'evening',
            'good', 'bad', 'happy', 'sad', 'angry',
            'big', 'small', 'tall', 'short', 'long',
            'hot', 'cold', 'warm', 'cool', 'wet',
            'dry', 'soft', 'hard', 'heavy', 'light',
            'fast', 'slow', 'new', 'old', 'young',
            'beautiful', 'ugly', 'clean', 'dirty', 'bright',
            'dark', 'loud', 'quiet', 'easy', 'difficult',
            'love', 'like', 'hate', 'want', 'need',
            'have', 'do', 'go', 'come', 'see',
            'hear', 'say', 'tell', 'ask', 'answer',
            'think', 'know', 'learn', 'teach', 'read',
            'write', 'draw', 'paint', 'sing', 'dance',
            'play', 'work', 'study', 'eat', 'drink',
            'sleep', 'walk', 'run', 'jump', 'swim',
            'fly', 'drive', 'ride', 'cook', 'clean'
        ],
        hintDatabase: {
            // 分类提示
            'fruit': ['这是一种水果', '可以直接食用', '富含维生素'],
            'vegetable': ['这是一种蔬菜', '需要烹饪', '营养丰富'],
            'food': ['这是一种食物', '可以食用', '美味可口'],
            'drink': ['这是一种饮料', '可以喝', '解渴'],
            'furniture': ['这是家具', '用于家居', '提供便利'],
            'stationery': ['这是文具', '用于学习', '帮助书写'],
            'electronics': ['这是电子产品', '需要用电', '现代科技'],
            'clothing': ['这是服装', '用于穿着', '保护身体'],
            'animal': ['这是动物', '有生命', '生活在自然界'],
            'color': ['这是颜色', '用于描绘', '丰富多彩'],
            'number': ['这是数字', '用于计数', '数学基础'],
            'month': ['这是月份', '表示时间', '一年中的月份'],
            'weekday': ['这是星期', '一周中的一天', '工作日或周末'],
            'body': ['这是身体部位', '人体的一部分', '有特定功能'],
            'transport': ['这是交通工具', '用于出行', '快速到达'],
            'weather': ['这是天气现象', '自然现象', '影响日常生活'],
            'adjective': ['这是形容词', '描述事物', '表达特征'],
            'verb': ['这是动词', '表示动作', '描述行为'],
            'noun': ['这是名词', '表示事物', '具体或抽象'],
            'time': ['这是时间相关词汇', '表示时间', '生活节奏'],
            'place': ['这是地点', '表示位置', '生活场所']
        },
        wordCategories: {
            // 单词分类映射
            'apple': 'fruit', 'banana': 'fruit', 'orange': 'fruit', 'grape': 'fruit', 'melon': 'fruit',
            'bread': 'food', 'cake': 'food', 'pizza': 'food', 'burger': 'food', 'fries': 'food',
            'water': 'drink', 'juice': 'drink', 'coffee': 'drink', 'tea': 'drink', 'milk': 'drink',
            'chair': 'furniture', 'table': 'furniture', 'sofa': 'furniture', 'bed': 'furniture', 'desk': 'furniture',
            'book': 'stationery', 'pen': 'stationery', 'pencil': 'stationery', 'paper': 'stationery', 'notebook': 'stationery',
            'phone': 'electronics', 'computer': 'electronics', 'tv': 'electronics', 'radio': 'electronics', 'camera': 'electronics',
            'clock': 'electronics', 'watch': 'electronics', 'mirror': 'furniture', 'lamp': 'electronics', 'light': 'electronics',
            'door': 'furniture', 'window': 'furniture', 'wall': 'furniture', 'floor': 'furniture', 'ceiling': 'furniture',
            'shoe': 'clothing', 'sock': 'clothing', 'shirt': 'clothing', 'pants': 'clothing', 'dress': 'clothing',
            'hat': 'clothing', 'glasses': 'clothing', 'umbrella': 'clothing', 'bag': 'clothing', 'wallet': 'clothing',
            'dog': 'animal', 'cat': 'animal', 'bird': 'animal', 'fish': 'animal', 'mouse': 'animal',
            'horse': 'animal', 'cow': 'animal', 'sheep': 'animal', 'pig': 'animal', 'chicken': 'animal',
            'duck': 'animal', 'goose': 'animal', 'rabbit': 'animal', 'tiger': 'animal', 'lion': 'animal',
            'bear': 'animal', 'monkey': 'animal', 'elephant': 'animal', 'giraffe': 'animal', 'zebra': 'animal',
            'deer': 'animal', 'fox': 'animal', 'wolf': 'animal',
            'red': 'color', 'blue': 'color', 'green': 'color', 'yellow': 'color', 'orange': 'color',
            'purple': 'color', 'pink': 'color', 'brown': 'color', 'black': 'color', 'white': 'color',
            'gray': 'color', 'gold': 'color', 'silver': 'color', 'bronze': 'color', 'copper': 'color',
            'one': 'number', 'two': 'number', 'three': 'number', 'four': 'number', 'five': 'number',
            'six': 'number', 'seven': 'number', 'eight': 'number', 'nine': 'number', 'ten': 'number',
            'eleven': 'number', 'twelve': 'number', 'thirteen': 'number', 'fourteen': 'number', 'fifteen': 'number',
            'january': 'month', 'february': 'month', 'march': 'month', 'april': 'month', 'may': 'month',
            'june': 'month', 'july': 'month', 'august': 'month', 'september': 'month', 'october': 'month',
            'november': 'month', 'december': 'month',
            'monday': 'weekday', 'tuesday': 'weekday', 'wednesday': 'weekday', 'thursday': 'weekday', 'friday': 'weekday',
            'saturday': 'weekday', 'sunday': 'weekday',
            'head': 'body', 'eye': 'body', 'ear': 'body', 'nose': 'body', 'mouth': 'body',
            'hand': 'body', 'foot': 'body', 'arm': 'body', 'leg': 'body', 'finger': 'body',
            'toe': 'body', 'hair': 'body', 'face': 'body', 'neck': 'body', 'shoulder': 'body',
            'car': 'transport', 'bus': 'transport', 'train': 'transport', 'plane': 'transport', 'bike': 'transport',
            'boat': 'transport', 'ship': 'transport', 'truck': 'transport', 'taxi': 'transport',
            'sun': 'weather', 'rain': 'weather', 'snow': 'weather', 'wind': 'weather', 'cloud': 'weather',
            'storm': 'weather', 'fog': 'weather', 'hail': 'weather', 'thunder': 'weather', 'lightning': 'weather',
            'home': 'place', 'school': 'place', 'work': 'place', 'park': 'place', 'store': 'place',
            'city': 'place', 'town': 'place', 'village': 'place', 'country': 'place', 'world': 'place',
            'day': 'time', 'night': 'time', 'time': 'time', 'year': 'time', 'month': 'time',
            'week': 'time', 'hour': 'time', 'minute': 'time', 'second': 'time', 'today': 'time',
            'yesterday': 'time', 'tomorrow': 'time', 'morning': 'time', 'afternoon': 'time', 'evening': 'time',
            'good': 'adjective', 'bad': 'adjective', 'happy': 'adjective', 'sad': 'adjective', 'angry': 'adjective',
            'big': 'adjective', 'small': 'adjective', 'tall': 'adjective', 'short': 'adjective', 'long': 'adjective',
            'hot': 'adjective', 'cold': 'adjective', 'warm': 'adjective', 'cool': 'adjective', 'wet': 'adjective',
            'dry': 'adjective', 'soft': 'adjective', 'hard': 'adjective', 'heavy': 'adjective', 'light': 'adjective',
            'fast': 'adjective', 'slow': 'adjective', 'new': 'adjective', 'old': 'adjective', 'young': 'adjective',
            'beautiful': 'adjective', 'ugly': 'adjective', 'clean': 'adjective', 'dirty': 'adjective', 'bright': 'adjective',
            'dark': 'adjective', 'loud': 'adjective', 'quiet': 'adjective', 'easy': 'adjective', 'difficult': 'adjective',
            'love': 'verb', 'like': 'verb', 'hate': 'verb', 'want': 'verb', 'need': 'verb',
            'have': 'verb', 'do': 'verb', 'go': 'verb', 'come': 'verb', 'see': 'verb',
            'hear': 'verb', 'say': 'verb', 'tell': 'verb', 'ask': 'verb', 'answer': 'verb',
            'think': 'verb', 'know': 'verb', 'learn': 'verb', 'teach': 'verb', 'read': 'verb',
            'write': 'verb', 'draw': 'verb', 'paint': 'verb', 'sing': 'verb', 'dance': 'verb',
            'play': 'verb', 'work': 'verb', 'study': 'verb', 'eat': 'verb', 'drink': 'verb',
            'sleep': 'verb', 'walk': 'verb', 'run': 'verb', 'jump': 'verb', 'swim': 'verb',
            'fly': 'verb', 'drive': 'verb', 'ride': 'verb', 'cook': 'verb', 'clean': 'verb'
        }
    },
    computed: {
        accuracy() {
            const totalGuesses = this.correctGuesses.length + this.wrongGuesses.length;
            if (totalGuesses === 0) return 0;
            return Math.round((this.correctGuesses.length / totalGuesses) * 100);
        }
    },
    methods: {
        // 开始游戏
        startGame() {
            if (this.maxAttempts < 5 || this.maxAttempts > 20) {
                alert('请输入5-20之间的数字');
                return;
            }
            
            // 随机选择目标单词
            this.targetWord = this.wordDatabase[Math.floor(Math.random() * this.wordDatabase.length)];
            
            // 初始化游戏状态
            this.remainingAttempts = this.maxAttempts;
            this.correctGuesses = [];
            this.wrongGuesses = [];
            this.hints = [];
            this.currentGuess = '';
            
            // 初始化显示单词
            this.displayWord = Array(this.targetWord.length).fill('_');
            
            // 设置游戏状态为进行中
            this.gameState = 'playing';
        },
        
        // 重新开始游戏
        restartGame() {
            this.gameState = 'setup';
            this.maxAttempts = 10;
            this.targetWord = '';
            this.displayWord = [];
            this.correctGuesses = [];
            this.wrongGuesses = [];
            this.hints = [];
            this.currentGuess = '';
        },
        
        // 处理输入框输入
        handleInput() {
            // 只保留字母并转换为大写
            this.currentGuess = this.currentGuess.replace(/[^a-zA-Z]/g, '').toUpperCase();
        },
        
        // 处理键盘按键
        handleKeyUp(event) {
            // 处理回车键
            if (event.key === 'Enter') {
                this.makeGuess();
            }
            // 处理退格键
            else if (event.key === 'Backspace') {
                this.currentGuess = '';
            }
            // 处理字母键 - 直接进行猜测
            else if (/^[a-zA-Z]$/.test(event.key)) {
                this.makeGuess(event.key);
            }
        },
        
        // 进行猜测
        makeGuess(letter = null) {
            // 获取猜测的字母并转换为小写
            let guess = '';
            
            if (letter) {
                // 处理虚拟键盘点击，确保是单个字母
                guess = letter.toString().trim().toLowerCase().charAt(0);
            } else {
                // 处理输入框输入
                guess = this.currentGuess.trim().toLowerCase().charAt(0);
            }
            
            // 验证输入
            if (!guess || !/^[a-z]$/.test(guess)) {
                alert('请输入一个有效的字母');
                this.currentGuess = '';
                return;
            }
            
            // 检查是否已经猜过
            if (this.correctGuesses.includes(guess) || this.wrongGuesses.includes(guess)) {
                alert('你已经猜过这个字母了');
                this.currentGuess = '';
                return;
            }
            
            // 检查字母是否在目标单词中
            if (this.targetWord.includes(guess)) {
                // 正确猜测
                this.correctGuesses.push(guess);
                
                // 更新显示单词
                for (let i = 0; i < this.targetWord.length; i++) {
                    if (this.targetWord[i] === guess) {
                        this.displayWord[i] = guess.toUpperCase();
                    }
                }
                
                // 检查是否赢得游戏
                if (!this.displayWord.includes('_')) {
                    this.gameState = 'won';
                }
            } else {
                // 错误猜测
                this.wrongGuesses.push(guess);
                this.remainingAttempts--;
                
                // 检查是否获得新提示
                this.checkForHint();
                
                // 检查是否输掉游戏
                if (this.wrongGuesses.length >= this.maxAttempts) {
                    this.gameState = 'lost';
                    this.remainingAttempts = 0;
                }
            }
            
            // 清空输入
            this.currentGuess = '';
        },
        
        // 检查是否获得提示
        checkForHint() {
            const wrongCount = this.wrongGuesses.length;
            const hintCount = Math.floor(wrongCount / 3);
            
            // 如果应该获得新提示且还没有达到最大提示数
            if (hintCount > this.hints.length) {
                this.addHint();
            }
        },
        
        // 添加提示
        addHint() {
            const category = this.wordCategories[this.targetWord];
            const categoryHints = this.hintDatabase[category] || this.hintDatabase['noun'];
            
            // 随机选择一个提示
            const randomHint = categoryHints[Math.floor(Math.random() * categoryHints.length)];
            
            // 确保不会重复添加相同的提示
            if (!this.hints.includes(randomHint)) {
                this.hints.push(randomHint);
            } else {
                // 如果提示已存在，尝试添加另一个
                this.addHint();
            }
        },
        
        // 检查字母是否已使用
        isLetterUsed(letter) {
            const lowerLetter = letter.toLowerCase();
            return this.correctGuesses.includes(lowerLetter) || this.wrongGuesses.includes(lowerLetter);
        },
        
        // 检查字母是否正确
        isLetterCorrect(letter) {
            const lowerLetter = letter.toLowerCase();
            return this.correctGuesses.includes(lowerLetter);
        },
        
        // 检查字母是否错误
        isLetterWrong(letter) {
            const lowerLetter = letter.toLowerCase();
            return this.wrongGuesses.includes(lowerLetter);
        }
    }
});
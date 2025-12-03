new Vue({
    el: '#app',
    data: {
        // 游戏状态
        gameStarted: false,
        gameEnded: false,
        playerWon: false,
        
        // 游戏设置
        maxGuesses: null,
        remainingGuesses: null,
        
        // 游戏数据
        targetWord: '',
        displayedWord: [],
        guessedLetters: [],
        
        // 玩家输入
        userInput: '',
        
        // 提示系统
        hintPoints: 0,
        currentHint: '',
        hintsUsed: 0,
        
        // 消息提示
        message: '',
        messageType: '',
        
        // 单词库（100+ 常用单词）
        wordLibrary: [
            // 动物类
            'cat', 'dog', 'bird', 'fish', 'elephant', 'tiger', 'lion', 'bear', 'monkey', 'zebra',
            'giraffe', 'penguin', 'dolphin', 'whale', 'shark', 'snake', 'lizard', 'frog',
            
            // 水果类
            'apple', 'banana', 'orange', 'grape', 'watermelon', 'strawberry', 'blueberry', 'raspberry',
            'mango', 'pineapple', 'peach', 'pear', 'plum', 'cherry', 'lemon', 'lime',
            
            // 蔬菜类
            'carrot', 'potato', 'tomato', 'cucumber', 'lettuce', 'spinach', 'broccoli', 'cauliflower',
            'onion', 'garlic', 'pepper', 'corn', 'peas', 'beans',
            
            // 日常用品
            'phone', 'computer', 'tablet', 'keyboard', 'mouse', 'chair', 'desk', 'lamp', 'clock', 'watch',
            'glasses', 'hat', 'shirt', 'pants', 'shoes', 'socks', 'bag', 'backpack', 'wallet',
            
            // 食物类
            'bread', 'cake', 'cookie', 'chocolate', 'icecream', 'pizza', 'hamburger', 'sandwich', 'salad',
            'soup', 'rice', 'noodle', 'egg', 'milk', 'cheese', 'butter',
            
            // 自然类
            'sun', 'moon', 'star', 'sky', 'cloud', 'rain', 'snow', 'wind', 'fire', 'water',
            'earth', 'mountain', 'hill', 'river', 'lake', 'ocean', 'tree', 'flower', 'grass', 'leaf',
            
            // 交通工具
            'car', 'bus', 'train', 'plane', 'boat', 'bike', 'bicycle', 'motorcycle', 'truck', 'taxi',
            
            // 颜色类
            'red', 'blue', 'green', 'yellow', 'purple', 'orange', 'pink', 'black', 'white', 'brown',
            
            // 其他常用单词
            'home', 'house', 'room', 'door', 'window', 'floor', 'wall', 'roof',
            'book', 'pen', 'pencil', 'paper', 'notebook', 'eraser', 'ruler',
            'music', 'song', 'dance', 'art', 'painting', 'picture', 'photo',
            'sport', 'game', 'ball', 'run', 'jump', 'swim', 'fly',
            'happy', 'sad', 'angry', 'excited', 'tired', 'hungry', 'thirsty',
            'big', 'small', 'tall', 'short', 'long', 'wide', 'round', 'square'
        ],
        
        // 提示数据库
        hintDatabase: {
            // 动物类提示
            'cat': ['喜欢吃鱼', '会抓老鼠', '发出喵喵声', '有九条命的传说'],
            'dog': ['人类最好的朋友', '会看家护院', '喜欢啃骨头', '对主人忠诚'],
            'bird': ['有翅膀会飞', '会唱歌', '有羽毛', '住在鸟巢里'],
            'fish': ['生活在水里', '用鳃呼吸', '会游泳', '有鳞片'],
            'elephant': ['体型非常庞大', '有长长的鼻子', '有大大的耳朵', '非常聪明'],
            'tiger': ['森林之王', '有条纹的毛皮', '非常凶猛', '速度很快'],
            'lion': ['草原之王', '雄性有鬃毛', '群居生活', '非常勇敢'],
            'bear': ['体型健壮', '有厚厚的毛皮', '喜欢吃蜂蜜', '冬天会冬眠'],
            'monkey': ['喜欢爬树', '非常灵活', '喜欢吃香蕉', '模仿能力强'],
            'zebra': ['身上有黑白条纹', '生活在草原', '跑得很快', '是马的近亲'],
            'giraffe': ['有长长的脖子', '个子很高', '喜欢吃树叶', '身上有斑纹'],
            'penguin': ['不会飞', '会游泳', '生活在寒冷的地方', '走路摇摆'],
            'dolphin': ['生活在海里', '非常聪明', '会跳跃', '喜欢群居'],
            'whale': ['体型巨大', '生活在海洋', '会喷水', '是哺乳动物'],
            'shark': ['海洋中的凶猛鱼类', '有锋利的牙齿', '游泳速度快', '嗅觉灵敏'],
            'snake': ['没有四肢', '身体细长', '有的有毒', '会蜕皮'],
            'lizard': ['有四肢', '有鳞片', '有的会变色', '生活在温暖的地方'],
            'frog': ['两栖动物', '会跳', '以昆虫为食', '叫声响亮'],
            
            // 水果类提示
            'apple': ['红色或绿色', '乔布斯创立的公司标志', '一天一个医生远离我', '常用来做派'],
            'banana': ['黄色', '弯弯的形状', '富含钾元素', '猴子喜欢吃'],
            'orange': ['橙色', '富含维生素C', '可以榨果汁', '有厚厚的皮'],
            'grape': ['紫色或绿色', '成串生长', '可以酿酒', '味道酸甜'],
            'watermelon': ['夏天常见', '红色果肉', '多汁解渴', '有黑色的籽'],
            'strawberry': ['红色', '表面有籽', '味道甜美', '常用来做甜点'],
            'blueberry': ['蓝色', '小而圆', '富含抗氧化剂', '对眼睛好'],
            'raspberry': ['红色', '表面有小颗粒', '味道酸甜', '常用来做果酱'],
            'mango': ['黄色或橙色', '热带水果', '味道甜美', '果肉多汁'],
            'pineapple': ['黄色', '表面有刺', '热带水果', '味道酸甜'],
            'peach': ['粉色或黄色', '果肉柔软', '味道甜美', '常用来做罐头'],
            'pear': ['黄色或绿色', '形状像葫芦', '果肉脆甜', '水分多'],
            'plum': ['紫色或红色', '果肉柔软', '味道酸甜', '有的品种很酸'],
            'cherry': ['红色', '小而圆', '味道甜美', '常用来装饰蛋糕'],
            'lemon': ['黄色', '味道很酸', '富含维生素C', '常用来调味'],
            'lime': ['绿色', '味道酸', '常用来做鸡尾酒', '热带水果'],
            
            // 蔬菜类提示
            'carrot': ['橙色', '长条形', '富含胡萝卜素', '对眼睛好'],
            'potato': ['棕色', '圆形或椭圆形', '富含淀粉', '常用来做 fries'],
            'tomato': ['红色', '有的是黄色', '可以生吃或熟吃', '常用来做番茄酱'],
            'cucumber': ['绿色', '长条形', '水分多', '常用来做沙拉'],
            'lettuce': ['绿色', '叶子状', '常用来做沙拉的基底', '水分多'],
            'spinach': ['绿色', '叶子状', '富含铁元素', '常用来做沙拉或烹饪'],
            'broccoli': ['绿色', '花状', '富含维生素', '常用来蒸或炒'],
            'cauliflower': ['白色', '花状', '可以做成多种菜肴', '低热量'],
            'onion': ['白色或紫色', '有强烈的气味', '常用来调味', '会让人流泪'],
            'garlic': ['白色', '有强烈的气味', '常用来调味', '有健康益处'],
            'pepper': ['红色、绿色或黄色', '长条形或圆形', '有的辣有的不辣', '常用来做菜'],
            'corn': ['黄色', '颗粒状', '常用来煮或烤', '夏天常见'],
            'peas': ['绿色', '小颗粒', '常用来做配菜', '富含蛋白质'],
            'beans': ['多种颜色', '长条形', '常用来做汤或炖菜', '富含蛋白质'],
            
            // 日常用品提示（通用）
            'phone': ['通讯工具', '可以打电话发短信', '智能设备', '随身携带'],
            'computer': ['办公工具', '可以上网', '有键盘鼠标', '处理信息'],
            'tablet': ['平板设备', '介于手机和电脑之间', '可以触摸操作', '方便携带'],
            'keyboard': ['输入设备', '有很多按键', '用来打字', '连接电脑'],
            'mouse': ['输入设备', '可以移动光标', '有按键', '操作电脑'],
            'chair': ['家具', '用来坐', '有靠背', '办公室常见'],
            'desk': ['家具', '用来写字或工作', '有桌面', '办公室常见'],
            'lamp': ['照明工具', '可以发光', '有灯罩', '放在桌子上'],
            'clock': ['计时工具', '显示时间', '有的会报时', '挂在墙上'],
            'watch': ['计时工具', '戴在手腕上', '显示时间', '装饰用品'],
            'glasses': ['视力辅助工具', '有镜片', '戴在眼睛上', '矫正视力'],
            'hat': ['服饰', '戴在头上', '遮阳或保暖', '装饰用品'],
            'shirt': ['服饰', '穿在身上', '有袖子', '日常穿着'],
            'pants': ['服饰', '穿在腿上', '有裤腿', '日常穿着'],
            'shoes': ['服饰', '穿在脚上', '保护脚', '日常穿着'],
            'socks': ['服饰', '穿在脚上', '保护脚', '保暖'],
            'bag': ['用品', '用来装东西', '手提或肩背', '方便携带'],
            'backpack': ['用品', '背在背上', '用来装东西', '学生常见'],
            'wallet': ['用品', '用来装钱和卡片', '放在口袋里', '随身携带'],
            
            // 食物类提示（通用）
            'bread': ['主食', '用面粉做的', '可以夹东西吃', '早餐常见'],
            'cake': ['甜点', '有甜味', '生日时常见', '上面有奶油'],
            'cookie': ['甜点', '小而圆', '有甜味', '常作为零食'],
            'chocolate': ['甜点', '棕色', '有甜味', '很多人喜欢吃'],
            'icecream': ['甜点', '冷的', '有甜味', '夏天常见'],
            'pizza': ['食品', '圆形', '有奶酪和 toppings', '意大利美食'],
            'hamburger': ['食品', '有面包和肉', '快餐店常见', '夹有蔬菜'],
            'sandwich': ['食品', '用面包夹东西', '方便食用', '午餐常见'],
            'salad': ['食品', '有蔬菜和其他 ingredients', '健康食品', '可以生吃'],
            'soup': ['食品', '液体状', '有营养', '冬天常见'],
            'rice': ['主食', '白色', '亚洲常见', '搭配菜肴'],
            'noodle': ['主食', '细长条', '亚洲常见', '可以煮或炒'],
            'egg': ['食品', '椭圆形', '有营养', '早餐常见'],
            'milk': ['饮品', '白色', '富含钙', '早餐常见'],
            'cheese': ['食品', '由牛奶制成', '有很多种类', '常用来做披萨'],
            'butter': ['食品', '黄色', '由牛奶制成', '常用来涂面包'],
            
            // 自然类提示（通用）
            'sun': ['天体', '白天出现', '发光发热', '太阳系中心'],
            'moon': ['天体', '晚上出现', '反射太阳光', '地球的卫星'],
            'star': ['天体', '晚上出现', '闪烁发光', '非常遥远'],
            'sky': ['自然现象', '在头顶上方', '蓝色', '有云'],
            'cloud': ['自然现象', '白色或灰色', '在天空中', '会下雨'],
            'rain': ['自然现象', '水从天空落下', '潮湿', '滋润大地'],
            'snow': ['自然现象', '白色', '冬天出现', '寒冷'],
            'wind': ['自然现象', '空气流动', '会吹动物体', '有不同强度'],
            'fire': ['自然现象', '发光发热', '可以取暖', '需要小心'],
            'water': ['自然物质', '液体', '生命必需', '透明'],
            'earth': ['行星', '我们居住的地方', '有陆地和海洋', '圆形'],
            'mountain': ['自然地貌', '很高', '有山峰', '可以爬山'],
            'hill': ['自然地貌', '比山矮', '坡度平缓', '可以行走'],
            'river': ['自然地貌', '水流', '很长', '注入海洋或湖泊'],
            'lake': ['自然地貌', '大面积的水', '周围有陆地', '可以游泳'],
            'ocean': ['自然地貌', '很大的水域', '咸水', '覆盖地球大部分表面'],
            'tree': ['植物', '很高', '有树干和树枝', '提供氧气'],
            'flower': ['植物', '有颜色', '很漂亮', '有香味'],
            'grass': ['植物', '绿色', '很短', '覆盖地面'],
            'leaf': ['植物', '绿色', '长在树枝上', '进行光合作用'],
            
            // 交通工具提示（通用）
            'car': ['交通工具', '四个轮子', '在公路上行驶', '私人所有'],
            'bus': ['交通工具', '大型', '公共交通', '有很多座位'],
            'train': ['交通工具', '在轨道上行驶', '很长', '可以运输很多人'],
            'plane': ['交通工具', '在空中飞行', '很快', '可以跨国旅行'],
            'boat': ['交通工具', '在水上行驶', '有桨或发动机', '可以捕鱼或娱乐'],
            'bike': ['交通工具', '两个轮子', '人力驱动', '环保'],
            'bicycle': ['交通工具', '两个轮子', '人力驱动', '环保健康'],
            'motorcycle': ['交通工具', '两个轮子', '发动机驱动', '很快'],
            'truck': ['交通工具', '大型', '用来运输货物', '有很强的载重能力'],
            'taxi': ['交通工具', '小型', '提供出租服务', '按里程收费'],
            
            // 颜色类提示（通用）
            'red': ['颜色', '热情', '苹果的颜色', '交通灯的停止信号'],
            'blue': ['颜色', '冷静', '天空的颜色', '海洋的颜色'],
            'green': ['颜色', '自然', '草的颜色', '环保的象征'],
            'yellow': ['颜色', '阳光', '香蕉的颜色', '交通灯的警告信号'],
            'purple': ['颜色', '神秘', '葡萄的颜色', '皇室的象征'],
            'orange': ['颜色', '活力', '橙子的颜色', '日落的颜色'],
            'pink': ['颜色', '可爱', '草莓的颜色', '常与女性相关'],
            'black': ['颜色', '神秘', '夜晚的颜色', '正式场合的颜色'],
            'white': ['颜色', '纯洁', '雪的颜色', '婚礼的颜色'],
            'brown': ['颜色', '自然', '泥土的颜色', '树干的颜色'],
            
            // 其他通用提示
            'home': ['地方', '居住的地方', '温暖舒适', '家人在一起'],
            'house': ['建筑', '用来居住', '有房间', '有屋顶'],
            'room': ['空间', '房子的一部分', '有门', '可以用来睡觉或工作'],
            'door': ['建筑部件', '用来进出', '可以打开或关闭', '有把手'],
            'window': ['建筑部件', '在墙上', '可以看到外面', '有玻璃'],
            'floor': ['建筑部件', '在脚下', '可以行走', '有不同的材料'],
            'wall': ['建筑部件', '垂直的', '用来分隔空间', '可以装饰'],
            'roof': ['建筑部件', '在顶部', '用来遮雨', '有不同的形状'],
            'book': ['物品', '有纸张', '可以阅读', '获取知识'],
            'pen': ['物品', '用来写字', '有墨水', '办公常见'],
            'pencil': ['物品', '用来写字', '有石墨', '可以擦除'],
            'paper': ['物品', '薄片状', '用来写字或打印', '办公常见'],
            'notebook': ['物品', '有很多纸张', '用来记笔记', '学生常见'],
            'eraser': ['物品', '用来擦除', '橡胶制成', '学生常见'],
            'ruler': ['物品', '用来测量', '直的', '有刻度'],
            'music': ['艺术', '有节奏', '可以听', '让人放松'],
            'song': ['音乐', '有歌词', '可以唱', '表达情感'],
            'dance': ['艺术', '有节奏', '可以跳', '表达情感'],
            'art': ['艺术', '创造美', '有很多形式', '表达想法'],
            'painting': ['艺术', '用颜料', '在画布上', '创作图像'],
            'picture': ['图像', '可以看', '记录瞬间', '有纪念意义'],
            'photo': ['图像', '用相机拍摄', '记录瞬间', '真实'],
            'sport': ['活动', '身体运动', '有规则', '保持健康'],
            'game': ['活动', '有规则', '可以玩', '娱乐'],
            'ball': ['物品', '圆形', '用来玩球', '运动常见'],
            'run': ['运动', '快速移动', '用腿', '保持健康'],
            'jump': ['运动', '向上移动', '离开地面', '有弹性'],
            'swim': ['运动', '在水中移动', '用手臂和腿', '夏天常见'],
            'fly': ['动作', '在空中移动', '有翅膀', '自由'],
            'happy': ['情绪', '快乐', '微笑', '积极'],
            'sad': ['情绪', '难过', '哭泣', '消极'],
            'angry': ['情绪', '生气', '愤怒', '激动'],
            'excited': ['情绪', '兴奋', '激动', '期待'],
            'tired': ['状态', '疲劳', '想睡觉', '需要休息'],
            'hungry': ['状态', '饥饿', '想吃东西', '肚子饿'],
            'thirsty': ['状态', '口渴', '想喝水', '嘴巴干'],
            'big': ['形容词', '尺寸大', '与小相反', '巨大'],
            'small': ['形容词', '尺寸小', '与大相反', '微小'],
            'tall': ['形容词', '高度高', '与矮相反', '高大'],
            'short': ['形容词', '高度矮', '与高相反', '短小'],
            'long': ['形容词', '长度长', '与短相反', '漫长'],
            'wide': ['形容词', '宽度宽', '与窄相反', '宽广'],
            'round': ['形容词', '形状圆', '圆形', '球形'],
            'square': ['形容词', '形状方', '正方形', '四方的']
        }
    },
    
    methods: {
        // 开始游戏
        startGame() {
            if (!this.maxGuesses || this.maxGuesses < 1) {
                this.showMessage('请输入有效的猜测次数', 'error');
                return;
            }
            
            // 初始化游戏状态
            this.gameStarted = true;
            this.gameEnded = false;
            this.playerWon = false;
            
            // 初始化游戏数据
            this.remainingGuesses = this.maxGuesses;
            this.guessedLetters = [];
            this.hintPoints = 0;
            this.currentHint = '';
            this.hintsUsed = 0;
            
            // 随机选择目标单词
            this.selectTargetWord();
            
            // 初始化显示单词
            this.initDisplayedWord();
            
            this.showMessage('游戏开始！祝你好运！', 'info');
        },
        
        // 随机选择目标单词
        selectTargetWord() {
            const randomIndex = Math.floor(Math.random() * this.wordLibrary.length);
            this.targetWord = this.wordLibrary[randomIndex].toLowerCase();
        },
        
        // 初始化显示单词
        initDisplayedWord() {
            this.displayedWord = [];
            for (let i = 0; i < this.targetWord.length; i++) {
                this.displayedWord.push('_');
            }
        },
        
        // 玩家猜测
        makeGuess() {
            if (!this.userInput.trim()) {
                this.showMessage('请输入一个字母', 'error');
                return;
            }
            
            const letter = this.userInput.trim().toLowerCase();
            
            // 检查是否已经猜过这个字母
            if (this.guessedLetters.includes(letter)) {
                this.showMessage('你已经猜过这个字母了', 'error');
                this.userInput = '';
                return;
            }
            
            // 添加到已猜字母列表
            this.guessedLetters.push(letter);
            
            // 检查字母是否在目标单词中
            if (this.targetWord.includes(letter)) {
                // 猜对了
                this.updateDisplayedWord(letter);
                this.showMessage(`恭喜你！字母 "${letter}" 在单词中`, 'success');
                
                // 检查是否猜中了所有字母
                if (this.checkWin()) {
                    this.endGame(true);
                }
            } else {
                // 猜错了
                this.remainingGuesses--;
                this.showMessage(`很遗憾，字母 "${letter}" 不在单词中`, 'error');
                
                // 检查是否获得提示点数
                this.checkHintPoints();
                
                // 检查是否猜次数用完
                if (this.remainingGuesses <= 0) {
                    this.endGame(false);
                }
            }
            
            // 清空输入
            this.userInput = '';
        },
        
        // 更新显示的单词
        updateDisplayedWord(letter) {
            for (let i = 0; i < this.targetWord.length; i++) {
                if (this.targetWord[i] === letter) {
                    this.displayedWord[i] = letter;
                }
            }
        },
        
        // 检查是否获胜
        checkWin() {
            return this.displayedWord.join('') === this.targetWord;
        },
        
        // 检查是否获得提示点数
        checkHintPoints() {
            const wrongGuesses = this.maxGuesses - this.remainingGuesses;
            // 每猜错3次获得1个提示点数
            const newHintPoints = Math.floor(wrongGuesses / 3);
            
            if (newHintPoints > this.hintPoints) {
                const delta = newHintPoints - this.hintPoints;
                this.hintPoints = newHintPoints;
                this.showMessage(`恭喜你获得了 ${delta} 个提示点数！现在你有 ${this.hintPoints} 个提示点数`, 'info');
            }
        },
        
        // 使用提示
        useHint() {
            if (this.hintPoints <= 0) {
                this.showMessage('你没有提示点数了', 'error');
                return;
            }
            
            // 获取该单词的提示列表
            const hints = this.hintDatabase[this.targetWord] || ['抱歉，暂无提示信息'];
            
            // 获取可使用的提示数量（不超过提示点数和可用提示数）
            const availableHints = hints.slice(this.hintsUsed);
            const hintsToUse = Math.min(this.hintPoints, availableHints.length);
            
            if (hintsToUse > 0) {
                // 获取要添加的提示
                const newHints = availableHints.slice(0, hintsToUse);
                
                // 如果已经有提示，追加；否则直接赋值
                if (this.currentHint) {
                    this.currentHint += `\n${newHints.join('\n')}`;
                } else {
                    this.currentHint = newHints.join('\n');
                }
                
                // 更新已使用的提示数量
                this.hintsUsed += hintsToUse;
                
                // 消耗提示点数
                this.hintPoints -= hintsToUse;
                
                this.showMessage(`已使用 ${hintsToUse} 个提示词！`, 'info');
            } else {
                // 如果所有提示都用完了
                this.showMessage('已经没有更多提示了', 'error');
            }
        },
        
        // 结束游戏
        endGame(won) {
            this.gameEnded = true;
            this.playerWon = won;
            
            if (won) {
                this.showMessage(`🎉 恭喜你赢了！你用了 ${this.maxGuesses - this.remainingGuesses} 次猜测`, 'success');
            } else {
                this.showMessage(`😢 很遗憾，你输了！答案是 "${this.targetWord}"`, 'error');
            }
        },
        
        // 放弃游戏
        giveUp() {
            if (confirm('确定要放弃游戏吗？')) {
                this.endGame(false);
            }
        },
        
        // 重新开始游戏
        restartGame() {
            // 保留原来的最大猜测次数
            const oldMaxGuesses = this.maxGuesses;
            
            // 重新初始化游戏
            this.gameStarted = false;
            this.gameEnded = false;
            this.playerWon = false;
            this.maxGuesses = oldMaxGuesses;
            this.userInput = '';
            
            // 开始游戏
            this.startGame();
        },
        
        // 返回设置界面
        backToSetup() {
            this.gameStarted = false;
            this.gameEnded = false;
            this.playerWon = false;
            this.maxGuesses = null;
            this.userInput = '';
        },
        
        // 显示消息
        showMessage(text, type) {
            this.message = text;
            this.messageType = type;
            
            // 5秒后自动隐藏消息
            setTimeout(() => {
                this.message = '';
                this.messageType = '';
            }, 5000);
        }
    }
});
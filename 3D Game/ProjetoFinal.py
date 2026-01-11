import pygame
import sys
import random
import numpy as np
import math
import os

# Certifique-se de que estes imports correspondam à estrutura do seu projeto
from core.base import Base
from core_ext.camera import Camera
from core_ext.mesh import Mesh
from core_ext.renderer import Renderer
from core_ext.scene import Scene
from core_ext.texture import Texture
from core.obj_reader import ObjReader
from extras.axes import AxesHelper
from extras.grid import GridHelper
from extras.text_texture import TextTexture
from geometry.rectangle import RectangleGeometry
from geometry.sphere import SphereGeometry
from extras.movement_rig import MovementRig
from material.texture import TextureMaterial
from material.material import Material
from pygame.locals import *
from pygame import mixer

class Example(Base):
    def initialize(self):
        print("Initializing GAME...\nPlay Game : P\nMovement: AD(move)\nRestart : R")
        self.renderer = Renderer()
        self.scene = Scene()
        self.camera = Camera(aspect_ratio=800 / 600)
        self.camera_rig = MovementRig()
        self.camera_rig.add(self.camera)
        self.camera_rig.set_position([0, 0, 10])  # Ajuste a posição da câmera conforme necessário
        self.scene.add(self.camera_rig)
        pygame.mixer.init()
        # Relógio para controlar o tempo
        self.clock = pygame.time.Clock()
        
        # Load background music
        self.background_music = pygame.mixer.music.load("music/Sea.mp3")
        self.background_music = pygame.mixer.music.set_volume(0.1)
        # Start playing background music
        pygame.mixer.music.play(loops=-1)  # Loop indefinitely

        self.time_since_last_second = 0  # Variável para rastrear o tempo decorrido desde o último segundo
        
        # Initialize game state variables
        self.game_over = False  # Track game over state

        self.speed_increase_interval = 5  # Interval in seconds to increase speed
        self.last_speed_increase_time = 0  # Time when the speed was last increased
        
        self.ball_spawn_interval = 5  # Interval in seconds to spawn a new ball
        self.last_ball_spawn_time = 0  # Time when the last ball was spawned

        # Initialize prancha movement
        self.prancha_speed = 4  # Speed of the prancha
        self.prancha_position = np.array([1, 0.1, 399])  # Initial position of the prancha
        self.prancha_direction = np.array([0, 0, -1])  # Initial direction of movement
        self.prancha_lateral_speed = 2  # Lateral speed of the prancha
        self.movement_started = True  # Automatically start movement
    
        self.time = 0  # Variable to track the elapsed time
        self.score = 0  # Counter for score
        
        self.bolas = []  # Lista para armazenar todas as bolas
        self.boat_rigs = []  # Store references to all boat rigs
        
        self.game_over_mesh = None  # Store reference to the game over text mesh
        self.mar_rigs = []

        
        vertex_shader_code = """
            uniform mat4 projectionMatrix;
            uniform mat4 viewMatrix;
            uniform mat4 modelMatrix;
            in vec3 vertexPosition;
            in vec2 vertexUV;
            out vec2 UV;
            
            void main()
            {
                gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
                UV = vertexUV;
            }
        """
        fragment_shader_code = """
            uniform sampler2D rgbNoise;
            uniform sampler2D image;
            in vec2 UV;
            uniform float time;
            out vec4 fragColor;

            void main()
            {
                vec2 uvShift = UV + vec2(-0.033, 0.07) * time;
                vec4 noiseValues = texture2D(rgbNoise, uvShift);
                vec2 uvNoise = UV + 0.01 * noiseValues.rg;
                fragColor = texture2D(image, uvNoise);
            }
        """
        
        # Carregar e configurar o objeto da palmeira
        palmeira_filename = 'obj/palmtrees.obj'
        palmeira_geometry = ObjReader(palmeira_filename)
        palmeira_texture = Texture(file_name="images/palmtree.png")
        palmeira_material = TextureMaterial(texture=palmeira_texture)

        for i in range(0, 2):
            for j in range(-400, 400,7):
                x_position = -4 if i == 0 else 6  # Ajuste a posição x para os dois lados
                palmeira_mesh = Mesh(palmeira_geometry, palmeira_material)
                palmeira_rig = MovementRig()
                palmeira_rig.add(palmeira_mesh)
                palmeira_rig.set_position([x_position , -1, j * 1])
                self.scene.add(palmeira_rig)

        # Carregar e configurar o objeto do barco
        self.boat_filename = 'obj/barco.obj'
        boat_geometry = ObjReader(self.boat_filename)
        boat_texture = Texture(file_name="images/barco.jpeg")
        self.boat_material = TextureMaterial(texture=boat_texture)
        self.boat_mesh = Mesh(boat_geometry, self.boat_material)
        self.boat_rig = MovementRig()
        self.boat_rig.add(self.boat_mesh)
        self.boat_rig.set_position([-0.5, 0.26, 390])  # Ajuste essas coordenadas conforme necessário
        self.scene.add(self.boat_rig)
        self.boat_rigs.append(self.boat_rig)

        # Carregar e configurar o objeto da prancha
        prancha_filename = 'obj/prancha.obj'
        prancha_geometry = ObjReader(prancha_filename)
        prancha_texture = Texture(file_name="images/prancha.jpg")
        prancha_material = TextureMaterial(texture=prancha_texture)
        self.prancha_mesh = Mesh(prancha_geometry, prancha_material)
        self.prancha_rig = MovementRig()
        self.prancha_rig.add(self.prancha_mesh)
        self.prancha_rig.set_position(self.prancha_position)
        self.scene.add(self.prancha_rig)

        # Carregar e configurar o objeto do tubarão
        tubarao_filename = 'obj/sharke.obj'
        tubarao_geometry = ObjReader(tubarao_filename)
        tubarao_texture = Texture(file_name="images/sharke.jpg")
        tubarao_material = TextureMaterial(texture=tubarao_texture)
        self.tubarao_mesh = Mesh(tubarao_geometry, tubarao_material)
        self.tubarao_rig = MovementRig()
        self.tubarao_rig.add(self.tubarao_mesh)
        self.tubarao_rig.set_position([self.prancha_position[0], self.prancha_position[1], self.prancha_position[2] + 1])
        self.scene.add(self.tubarao_rig)
        
        # Carregar e configurar o objeto do surfista
        man_filename = 'obj/man.obj'
        man_geometry = ObjReader(man_filename)
        man_texture = Texture(file_name="images/man.jpg")
        man_material = TextureMaterial(texture=man_texture)
        self.man_mesh = Mesh(man_geometry, man_material)
        self.man_rig = MovementRig()
        self.man_rig.add(self.man_mesh)
        self.man_rig.set_position([self.prancha_position[0], self.prancha_position[1], self.prancha_position[2]])
        self.scene.add(self.man_rig)
        
        # Carregar e configurar o objeto mar
        mar_filename = 'obj/mar.obj'
        mar_geometry = ObjReader(mar_filename)
        mar_texture = Texture(file_name="images/mar.jpg")
        rgb_noise_texture = Texture("images/rgb-noise.jpg")
        
        self.distort_material = Material(vertex_shader_code, fragment_shader_code)
        self.distort_material.add_uniform("sampler2D", "rgbNoise", [rgb_noise_texture.texture_ref, 1])
        self.distort_material.add_uniform("sampler2D", "image", [mar_texture.texture_ref, 2])
        self.distort_material.add_uniform("float", "time", 0.0)

        self.distort_material.locate_uniforms()

        for i in range(0, 1):
            for j in range(-67, 67):
                mar_mesh = Mesh(mar_geometry, self.distort_material)
                mar_rig = MovementRig()
                mar_rig.add(mar_mesh)
                mar_rig.set_position([i * 0, -0.95, j * 6])
                self.scene.add(mar_rig)
                self.mar_rigs.append(mar_rig)

        sky_geometry = SphereGeometry(radius=400)
        sky_material = TextureMaterial(texture=Texture(file_name="images/sky.jpg"))
        sky = Mesh(sky_geometry, sky_material)
        self.scene.add(sky)

        sand_geometry = RectangleGeometry(width=800, height=800)
        sand_material = TextureMaterial(
            texture=Texture(file_name="images/sand.jpg"),
            property_dict={"repeatUV": [20, 20]}
        )
        sand = Mesh(sand_geometry, sand_material)
        sand.rotate_x(-math.pi / 2)
        sand.set_position([0, -0.19, 0])
        self.scene.add(sand)

        self.KEY_MOVE_LEFT = "a"
        self.KEY_MOVE_RIGHT = "d"
        self.KEY_START_STOP_MOVEMENT = "p"
        self.KEY_RESTART_GAME = "r"
        self.KEY_SPACE = "space"

        # Define initial boat positions
        self.next_boat_spawn_z = 380

    def create_game_over_text(self):
        game_over_geometry = RectangleGeometry(width=2.5 * 2, height=1.406 * 2)  # Reduzido para um tamanho menor
        game_over_texture = Texture(file_name="images/game_over2.png")
        game_over_material = TextureMaterial(texture=game_over_texture)
        self.game_over_mesh = Mesh(game_over_geometry, game_over_material)
        self.game_over_mesh.set_position([self.camera_rig.local_position[0], self.camera_rig.local_position[1] - 2.1, self.camera_rig.local_position[2] - 3])  # Ajustado para mais perto
        self.game_over_mesh.render_order = 1
        self.scene.add(self.game_over_mesh)

    def remove_objects_behind_prancha(self, object_list, distance_threshold, object_type):
        prancha_z = self.prancha_position[2]
        objects_to_remove = [obj for obj in object_list if obj.local_position[2] > prancha_z + distance_threshold]
        
        for obj in objects_to_remove:
            self.scene.remove(obj)
            object_list.remove(obj)
            print(f"{object_type} removido na posição Z:", obj.local_position[2])
    

    def create_ball(self):
        # Definindo o número máximo de bolas permitido
        max_bolas = 0.5
        
        # Verifica se o número atual de bolas é menor que o máximo permitido
        if len(self.bolas) < max_bolas:
            bola_filename = 'obj/volleyball.obj'
            bola_geometry = ObjReader(bola_filename)
            bola_texture = Texture(file_name="images/volleyball.jpg")
            bola_material = TextureMaterial(texture=bola_texture)
            bola_mesh = Mesh(bola_geometry, bola_material)
            bola_rig = MovementRig()
            bola_rig.add(bola_mesh)
            bola_rig.set_position(self.get_random_respawn_position())
            self.bolas.append(bola_rig)
            self.scene.add(bola_rig)
        else:
            print("Limite máximo de bolas alcançado.")

    def check_collision_with_boat(self):
        # Verifica se a posição atual da prancha está dentro dos limites do barco
        for boat_rig in self.boat_rigs:
            boat_position = boat_rig.local_position
            if (boat_position[0] - 0.95 < self.prancha_position[0] < boat_position[0] + 0.95) and \
                    (boat_position[2] - 0.95 < self.prancha_position[2] < boat_position[2] + 0.95):
                # Play collision sound
                collision_sound = pygame.mixer.Sound("music/Bite.mp3")
                collision_sound.play()
                collision_sound = pygame.mixer.Sound("music/AAHHH.wav")
                collision_sound.play()
                return True
        return False

    def check_collision_with_balls(self):
        # Verifica se a posição atual da prancha está dentro dos limites de alguma bola
        for bola_rig in self.bolas:
            ball_position = bola_rig.local_position
            if (self.prancha_position[0] - 0.7 < ball_position[0] < self.prancha_position[0] + 0.7) and \
                    (self.prancha_position[2] - 0.7 < ball_position[2] < self.prancha_position[2] + 0.7):
                self.score += 10  # Adiciona 10 pontos
                collision_sound = pygame.mixer.Sound("music/POP.mp3")
                collision_sound.play()
                self.bolas.remove(bola_rig)
                self.scene.remove(bola_rig)
                return True
        return False

    def check_collision_ball_with_boats(self):
        # Verifica se a posição atual de alguma bola está dentro dos limites do barco
        for bola_rig in self.bolas:
            ball_position = bola_rig.local_position
            for boat_rig in self.boat_rigs:
                boat_position = boat_rig.local_position
                if np.allclose(ball_position, boat_position, atol=1):
                    return True
        return False

    def get_random_respawn_position(self):
        # Retorna uma posição de respawn aleatória entre as três opções
        respawn_options = [[-1, 0, self.prancha_position[2] - 20],
                           [1, 0, self.prancha_position[2] - 20],
                           [3.5, 0, self.prancha_position[2] - 20]]
        return random.choice(respawn_options)

    def spawn_boats(self):
        current_z = self.prancha_rig.local_position[2]
        if current_z - 10 < self.next_boat_spawn_z:
            spawn_positions = [-1, 1, 3]
            num_boats_to_spawn = random.randint(1, 2)  # Spawns 1 or 2 boats
            for _ in range(num_boats_to_spawn):
                x = random.choice(spawn_positions)
                spawn_positions.remove(x)  # Ensure no two boats spawn at the same x position
                new_boat_rig = MovementRig()
                new_boat_mesh = Mesh(ObjReader(self.boat_filename), self.boat_material)
                new_boat_rig.add(new_boat_mesh)
                new_boat_rig.set_position([x, 0.26, self.next_boat_spawn_z])
                self.scene.add(new_boat_rig)
                self.boat_rigs.append(new_boat_rig)
            self.next_boat_spawn_z -= 15  # Update next spawn position
    
    def restart_game(self):
        # Redefine as variáveis do jogo para seus valores iniciais
        self.prancha_position = np.array([1, 0.1, 197])
        self.prancha_rig.set_position(self.prancha_position)
        self.tubarao_rig.set_position([self.prancha_position[0], self.prancha_position[1] - 3.6, self.prancha_position[2] + 2])
        self.score = 0
        self.bola_collision_count = 0
        self.bola_is_visible = True
        self.bola_respawn_position = None
        self.movement_started = True  # Automatically start movement
        self.game_over = False
        self.last_ball_spawn_time = 0  # Reset the ball spawn timer
        self.last_speed_increase_time = 0
       # Recriar objetos do mar
        self.initialize()

    def increase_speed_periodically(self):
        current_time = self.time  # Use the time variable to track elapsed time

        if current_time - self.last_speed_increase_time >= self.speed_increase_interval:
            if self.prancha_speed < 10:
                self.prancha_speed *= 1.05  # Aumenta a velocidade em 10%
            else:
                self.prancha_speed *= 1.05  # Aumenta a velocidade em 5% quando a velocidade atinge 10 ou mais

            if self.prancha_lateral_speed < 5:
                self.prancha_lateral_speed *= 1.1  # Aumenta a velocidade lateral em 10%
            else:
                self.prancha_lateral_speed *= 1.05  # Aumenta a velocidade lateral em 5% quando a velocidade atinge 5 ou mais

            self.last_speed_increase_time = current_time  # Atualiza o tempo da última vez que a velocidade foi aumentada

    def spawn_ball_periodically(self):
        current_time = self.time  # Use the time variable to track elapsed time

        if current_time - self.last_ball_spawn_time >= self.ball_spawn_interval:
            self.create_ball()
            self.last_ball_spawn_time = current_time  # Atualiza o tempo da última vez que a bola foi gerada
    
    def update(self):
        # Verifica se a tecla R foi pressionada para reiniciar o jogo
        if self.input.is_key_pressed(self.KEY_RESTART_GAME):
            self.restart_game()  # Chama o método restart_game() para reiniciar o jogo
        
        if not self.game_over:
            
            # Movimento automático da prancha
            self.time += self.delta_time  # Update the elapsed time

            self.prancha_position += self.prancha_speed * self.prancha_direction * self.delta_time

            # Movimento sinusoidal para simular o surfe
            sinusoidal_offset = 0.14 * math.sin(self.time * 1.1)  # Ajuste a amplitude e a frequência conforme desejado
            self.prancha_position[1] = 0.1 + sinusoidal_offset

            # Movimento lateral da prancha
            if self.input.is_key_pressed(self.KEY_MOVE_LEFT):
                self.prancha_position[0] -= self.prancha_lateral_speed * self.delta_time
            if self.input.is_key_pressed(self.KEY_MOVE_RIGHT):
                self.prancha_position[0] += self.prancha_lateral_speed * self.delta_time
            
            # Limitar o movimento lateral da prancha às três filas
            self.prancha_position[0] = np.clip(self.prancha_position[0], -1.9, 3.9)

            self.prancha_rig.set_position(self.prancha_position)
            
            # Atualizar a posição do homem para que fique em cima da prancha
            self.man_rig.set_position(self.prancha_position + np.array([0, 0.1, 0]))

            # Faz a câmera seguir a prancha
            self.camera_rig.set_position(self.prancha_position + np.array([0, 3.5, 3.5]))  # Ajuste a posição da câmera conforme desejado
            self.camera_rig.look_at(self.prancha_position)  # Mantém a câmera olhando para a prancha

            # Faz a tubarão seguir a prancha
            tubarao_direction = self.prancha_position - self.tubarao_rig.local_position
            tubarao_distance = np.linalg.norm(tubarao_direction)
            tubarao_direction = tubarao_direction / tubarao_distance if tubarao_distance != 0 else np.array([0, 0, 0])
            tubarao_speed = self.prancha_speed  # Velocidade do tubarão
            # Atualiza a posição do tubarão
            new_tubarao_position = self.tubarao_rig.local_position + tubarao_speed * tubarao_direction * self.delta_time
            # Mantém a altura Y do tubarão mais baixa que a prancha
            new_tubarao_position[1] = self.prancha_position[1] - 0.6  # Ajuste o valor conforme necessário

            self.increase_speed_periodically()
            self.spawn_ball_periodically()
            self.remove_objects_behind_prancha(self.bolas, 5, "Bola")
            self.remove_objects_behind_prancha(self.boat_rigs, 5, "Barco")
            self.remove_objects_behind_prancha(self.mar_rigs, 50, "Parte do mar")
            self.tubarao_rig.set_position(new_tubarao_position)

            self.spawn_boats()
            
            # Incrementar a pontuação a cada segundo
            self.time_since_last_second += self.delta_time
            if self.time_since_last_second >= 1:  # Se passou 1 segundo
                self.score += 1  # Incrementa a pontuação em 1 ponto
                self.time_since_last_second = 0  # Reseta o contador
            
            if self.check_collision_ball_with_boats():
                # Move a bola para uma nova posição ao longo do eixo x, mantendo a coordenada z
                for bola_rig in self.bolas:
                    new_x_position = random.choice([-1, 1, 3])  # Mova a bola aleatoriamente
                    bola_rig.set_position([new_x_position, bola_rig.local_position[1], bola_rig.local_position[2]])

            # Verifica se houve colisão com o barco
            if self.check_collision_with_boat():
                print("Game Over! Você colidiu com o barco.")
                 # Aumenta a velocidade do tubarão após a colisão
                self.prancha_speed = 0
                tubarao_speed *= 2  # Ajuste conforme necessário para fazer o tubarão nadar mais rápido

                # Faz o tubarão pular para cima da prancha
                self.tubarao_rig.set_position([self.prancha_position[0], self.prancha_position[1] - 0.5, self.prancha_position[2]])
                self.create_game_over_text()
                self.movement_started = False  # Pare o movimento
                self.game_over = True

            # Verifica se houve colisão com alguma bola
            if self.check_collision_with_balls():
                print("Colisão com a bola!")
                print(f"Score = {self.score}")
            
            # Remove a label anterior, se existir
            if hasattr(self, "label") and self.label in self.scene._children_list:
                self.scene.remove(self.label)
            # Cria a label
            label_texture = TextTexture(text=f"Score = {self.score}",
                                    system_font_name="Arial Bold",
                                    font_size=40, font_color=[0, 0, 0],
                                    background_color=[0, 0, 0, 0],
                                    image_width=256, image_height=128,
                                    align_horizontal=0.5, align_vertical=0.5,
                                    image_border_width=0,
                                    image_border_color=[0, 0, 0, 0])

            label_material = TextureMaterial(label_texture)
            label_geometry = RectangleGeometry(width=2, height=2)
            self.label = Mesh(label_geometry, label_material)
            self.label.set_position([self.camera_rig.local_position[0] - 2, self.camera_rig.local_position[1] - 1.5, self.camera_rig.local_position[2] - 4])
            self.scene.add(self.label)
            
            # Controla a taxa de frames por segundo (FPS)
            self.clock.tick(30)  # Mantém o jogo rodando a 30 frames por segundo
            self.distort_material.uniform_dict["time"].data += self.delta_time
            
        self.renderer.render(self.scene, self.camera)


# Pygame-based start screen
def start_screen():
    pygame.init()
    screen = pygame.display.set_mode((800, 600))
    pygame.display.set_caption("Start Screen")

    # Load the background image
    background_image = pygame.image.load("images/start_screen.jpg")
    background_image = pygame.transform.scale(background_image, (800, 600))

    font = pygame.font.Font(None, 74)
    play_button_text = font.render('Jogar', True, (54, 147, 216))

    # Define button properties
    button_color = (255, 255, 255)
    shadow_color = (255, 255, 255)
    button_rect = pygame.Rect(300, 250, 200, 100)
    play_text_rect = play_button_text.get_rect(center=button_rect.center)

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            elif event.type == pygame.MOUSEBUTTONDOWN:
                if button_rect.collidepoint(event.pos):
                    running = False

        screen.blit(background_image, (0, 0))

        # Draw the shadow
        shadow_offset = 5
        shadow_rect = button_rect.copy().move(shadow_offset, shadow_offset)
        pygame.draw.rect(screen, shadow_color, shadow_rect, border_radius=10)

        # Draw the button
        pygame.draw.rect(screen, button_color, button_rect, border_radius=10)
        screen.blit(play_button_text, play_text_rect)

        pygame.display.flip()

    pygame.quit()


if __name__ == "__main__":
    start_screen()
    Example(screen_size=[800, 600]).run()
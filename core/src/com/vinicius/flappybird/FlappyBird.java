package com.vinicius.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private BitmapFont fonte;
    private BitmapFont msgGameOver;

    //Declarando texturas
	private Texture[] passaro;
	private Texture fundo;
	private Texture canoTopo;
	private Texture canoBaixo;
	private Texture gameOver;

	//Declarando formas para colisões
	private Circle passaroCirculo;
    private Rectangle retCanoTopo;
	private Rectangle retCanoBaixo;
	//private ShapeRenderer shape;

	private int alturaTela;
    private int larguraTela;

    private Random numeroRandomico;
    private int modoJogo = 0;
    private int pontuacao = 0;
    private boolean pontuou = false;
    private int distanciaCanos = 400;
    private int movimentoCano = -300;
    private float variacaoPassaro = 0;
    private int posVerticalPassaro;
    private int velocidadeQueda = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        passaro = new Texture[3];
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        msgGameOver = new BitmapFont();
        msgGameOver.setColor(Color.WHITE);
        msgGameOver.getData().setScale(4);

        passaroCirculo = new Circle();
        retCanoTopo = new Rectangle();
        retCanoBaixo = new Rectangle();
        //shape = new ShapeRenderer();

        numeroRandomico = new Random();
        distanciaCanos = (numeroRandomico.nextInt(400) - 200);

        //Instanciando texturas
        fundo = new Texture("fundo.png");
        gameOver = new Texture("game_over.png");
        canoTopo = new Texture("cano_topo_maior.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");

        alturaTela = Gdx.graphics.getHeight();
        larguraTela = Gdx.graphics.getWidth();

        posVerticalPassaro = alturaTela/2;

	}

	@Override
	public void render () {
        batch.begin();
        batch.draw(fundo, 0, 0, larguraTela, alturaTela); //Desenha o fundo

        //determina queda do pássaro
        variacaoPassaro +=0.1;
        if(variacaoPassaro >= 3){
            variacaoPassaro = 0;
        }

		if(modoJogo == 0){ //Inicial
            if(Gdx.input.justTouched()) {
                modoJogo = 1;
            }
            msgGameOver.draw(batch, "Toque para iniciar", larguraTela/2-250, 200);

        }else if(modoJogo == 1){ //Jogo iniciado
            if (Gdx.input.justTouched())
                velocidadeQueda = -30;

            velocidadeQueda++;
            if (posVerticalPassaro > 0 || velocidadeQueda < 0)
                posVerticalPassaro -= (velocidadeQueda / 2);

            movimentoCano += 5;
            if (movimentoCano >= larguraTela + canoTopo.getWidth()) {
                movimentoCano = 0;
                distanciaCanos = (numeroRandomico.nextInt(400) - 200);
                pontuou = false;
            }

            if(movimentoCano > larguraTela-15){
                if(pontuou == false){
                    pontuacao++;
                    pontuou = true;
                }
            }

        }else{ // Game over
            velocidadeQueda++;
            if (posVerticalPassaro > 0 || velocidadeQueda < 0)
                posVerticalPassaro -= (velocidadeQueda / 2);

            if (Gdx.input.justTouched()){
                modoJogo = 0;
                posVerticalPassaro = alturaTela/2;
                velocidadeQueda = 0;
                movimentoCano = -300;
                pontuacao = 0;
            }
        }

        //Parâmetros das formas para colisões
        passaroCirculo.set(120+passaro[0].getWidth()/2, posVerticalPassaro+passaro[0].getHeight()/2, passaro[0].getWidth()/2-4);
        retCanoTopo.set(larguraTela - movimentoCano, (alturaTela / 2 + 200) - distanciaCanos, canoTopo.getWidth(), canoTopo.getHeight());
        retCanoBaixo.set(larguraTela - movimentoCano, ((alturaTela / 2 - 200) - canoBaixo.getHeight()) - distanciaCanos, canoBaixo.getWidth(), canoBaixo.getHeight());

        //Definindo parâmetros das formas para as colisões
        batch.draw(canoTopo, larguraTela - movimentoCano, (alturaTela / 2 + 200) - distanciaCanos);
        batch.draw(canoBaixo, larguraTela - movimentoCano, ((alturaTela / 2 - 200) - canoBaixo.getHeight()) - distanciaCanos);
        batch.draw(passaro[(int) variacaoPassaro], 120, posVerticalPassaro);
        fonte.draw(batch, String.valueOf(pontuacao), larguraTela/2, alturaTela-50);

        if(modoJogo == 2){
            batch.draw(gameOver, larguraTela/2-gameOver.getWidth()/2, alturaTela/2-gameOver.getHeight()/2);
            msgGameOver.draw(batch, "Toque para reiniciar", larguraTela/2-250, alturaTela/2-gameOver.getHeight());
        }

        batch.end();

        //Desenhar formas (pra teste)
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(retCanoTopo.x, retCanoTopo.y, retCanoTopo.getWidth(), retCanoTopo.getHeight());
        shape.rect(retCanoBaixo.x, retCanoBaixo.y, retCanoBaixo.getWidth(), retCanoBaixo.getHeight());
        shape.end();*/

        if(Intersector.overlaps(passaroCirculo, retCanoTopo) || Intersector.overlaps(passaroCirculo, retCanoBaixo) || posVerticalPassaro <= 0){
            modoJogo = 2;
        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		fundo.dispose();
		canoTopo.dispose();
		canoBaixo.dispose();
		fonte.dispose();
		passaro[0].dispose();
        passaro[1].dispose();
        passaro[2].dispose();
        passaro = null;
        //shape.dispose();
    }
}
